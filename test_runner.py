#!/usr/bin/env python3

import os
import sys
import subprocess
import time
import argparse
import glob
from pathlib import Path

# Configuration
SRC_DIR = "src"
OUTPUT_DIR = "output"
TEST_BASE_DIR = "testcases"
MAIN_CLASS = "Main"

# Type folder mapping
TYPE_DIRS = {
    'type1': 'Type1',
    'type2': 'Type2',
    'type3': 'Type3'
}

class Colors:
    def __init__(self):
        if os.name == 'nt':
            try:
                import colorama
                colorama.init()
                self.enabled = True
            except ImportError:
                self.enabled = False
        else:
            self.enabled = True
    
    def __getattr__(self, name):
        colors = {
            'GREEN': '\033[0;32m',
            'RED': '\033[0;31m', 
            'YELLOW': '\033[1;33m',
            'BLUE': '\033[0;34m',
            'NC': '\033[0m'
        }
        if self.enabled and name in colors:
            return colors[name]
        return ''

colors = Colors()

def log_info(message): print(f"{colors.BLUE}{message}{colors.NC}")
def log_success(message): print(f"{colors.GREEN}{message}{colors.NC}")
def log_warning(message): print(f"{colors.YELLOW}{message}{colors.NC}")
def log_error(message): print(f"{colors.RED}{message}{colors.NC}")

def ensure_directory(path):
    Path(path).mkdir(parents=True, exist_ok=True)

def compile_java():
    log_info("Compiling Java sources...")
    if not os.path.exists(SRC_DIR):
        log_error(f"✗ Source directory '{SRC_DIR}' not found")
        return False
    
    java_files = glob.glob(os.path.join(SRC_DIR, "*.java"))
    if not java_files:
        log_error(f"✗ No Java files found in '{SRC_DIR}'")
        return False
    
    try:
        cmd = ["javac"] + [os.path.basename(f) for f in java_files]
        result = subprocess.run(cmd, cwd=SRC_DIR, capture_output=True, text=True)
        if result.returncode != 0:
            log_error("✗ Compilation failed:")
            print(result.stderr)
            return False
        log_success("✓ Compilation successful")
        return True
    except Exception as e:
        log_error(f"✗ Compilation error: {e}")
        return False

def get_test_files(test_type=None):
    """TypeX/inputs klasöründeki dosyaları bulur"""
    if not os.path.exists(TEST_BASE_DIR):
        log_error(f"✗ Test directory '{TEST_BASE_DIR}' not found")
        return []
    
    files = []
    target_folders = []
    if test_type and test_type in TYPE_DIRS:
        target_folders.append(TYPE_DIRS[test_type])
    else:
        target_folders = list(TYPE_DIRS.values())

    for folder in target_folders:
        search_path = Path(TEST_BASE_DIR) / folder / "inputs" / "*.txt"
        found_files = glob.glob(str(search_path))
        files.extend(found_files)
    
    return sorted(files)

def run_single_test(input_file_path, verbose=False, benchmark=False):
    """Tek bir testi çalıştırır"""
    
    input_path = Path(input_file_path)
    basename = input_path.stem # 't1_large_1b' (uzantısız)
    
    # --- DÜZELTME BURADA YAPILDI ---
    # 1. Klasör değişimi: 'inputs' -> 'outputs'
    # 2. Dosya ismi değişimi: 'xxx.txt' -> 'xxx_output.txt'
    
    try:
        parts = list(input_path.parts)
        if 'inputs' in parts:
            # Klasörü değiştir
            idx = len(parts) - 1 - parts[::-1].index('inputs')
            parts[idx] = 'outputs'
            
            # Dosya ismini güncelle: t1_large_1b.txt -> t1_large_1b_output.txt
            old_filename = parts[-1]
            name_part = Path(old_filename).stem
            suffix_part = Path(old_filename).suffix
            new_filename = f"{name_part}_output{suffix_part}"
            parts[-1] = new_filename
            
            expected_path = Path(*parts)
        else:
            # Fallback (klasör yapısı beklenmedik ise basit replace)
            expected_path = Path(str(input_path).replace("inputs", "outputs").replace(".txt", "_output.txt"))
    except Exception:
         expected_path = Path(str(input_path).replace("inputs", "outputs"))

    # Bizim ürettiğimiz çıktı dosyası
    actual_path = Path(OUTPUT_DIR) / f"{basename}.txt"
    
    result = {
        'name': basename,
        'duration': 0,
        'status': 'unknown',
        'error_message': ''
    }
    
    # Expected file kontrolü
    if not benchmark and not expected_path.exists():
        result['status'] = 'skip'
        result['error_message'] = f"Missing expected file at:\n   -> {expected_path}"
        return result
    
    try:
        java_input = os.path.relpath(input_path, SRC_DIR)
        java_output = os.path.relpath(actual_path, SRC_DIR)
        
        cmd = ["java", MAIN_CLASS, java_input, java_output]
        
        start_time = time.time()
        process = subprocess.run(cmd, cwd=SRC_DIR, capture_output=True, text=True, timeout=30)
        result['duration'] = time.time() - start_time
        
        if process.returncode != 0:
            result['status'] = 'runtime_error'
            result['error_message'] = f"Exit code: {process.returncode}\nStderr: {process.stderr.strip()}"
            return result
        
        if not actual_path.exists():
            result['status'] = 'no_output'
            result['error_message'] = "Output file not created"
            return result
            
        if benchmark:
            result['status'] = 'benchmark_complete'
        else:
            # İçerik karşılaştırma (strip + line by line)
            with open(expected_path, 'r', encoding='utf-8', errors='ignore') as f1, \
                 open(actual_path, 'r', encoding='utf-8', errors='ignore') as f2:
                exp_lines = [l.strip() for l in f1.readlines() if l.strip()]
                act_lines = [l.strip() for l in f2.readlines() if l.strip()]
            
            if exp_lines == act_lines:
                result['status'] = 'pass'
            else:
                result['status'] = 'wrong_output'
                if verbose:
                    import difflib
                    diff = list(difflib.unified_diff(exp_lines, act_lines, fromfile='expected', tofile='actual', n=3))
                    result['error_message'] = '\n'.join(diff[:20])
                    
        return result

    except subprocess.TimeoutExpired:
        result['status'] = 'timeout'
        return result
    except Exception as e:
        result['status'] = 'error'
        result['error_message'] = str(e)
        return result

def run_tests(test_type=None, verbose=False, benchmark=False):
    input_files = get_test_files(test_type)
    
    if not input_files:
        log_warning(f"⚠ No test files found in {TEST_BASE_DIR}/Type*/inputs/")
        return {'total': 0, 'failed': 0}
        
    log_info(f"{'Benchmarking' if benchmark else 'Testing'} {len(input_files)} files...")
    print("-" * 60)
    ensure_directory(OUTPUT_DIR)
    
    stats = {'total': len(input_files), 'passed': 0, 'failed': 0, 'skipped': 0}
    
    for i, f in enumerate(input_files, 1):
        res = run_single_test(f, verbose, benchmark)
        
        status_color = colors.RED
        if res['status'] == 'pass' or res['status'] == 'benchmark_complete': status_color = colors.GREEN
        elif res['status'] == 'skip': status_color = colors.YELLOW
        
        status_text = res['status'].upper().replace('_', ' ')
        print(f"[{i}] {res['name']:<20} {status_color}{status_text:<10}{colors.NC}", end='')
        
        if res['status'] != 'skip':
            print(f" ({res['duration']:.2f}s)")
        else:
            print("")

        if res['status'] == 'skip' or (res['status'] != 'pass' and not benchmark):
            if verbose or res['status'] == 'skip':
                # Detayları göster
                print(f"   {colors.YELLOW}Details: {res.get('error_message', '')}{colors.NC}")
                if verbose and res.get('error_message') and res['status'] == 'wrong_output':
                     print("   --- Diff Start ---")
                     print(res['error_message'])
                     print("   --- Diff End ---")

        if res['status'] == 'pass': stats['passed'] += 1
        elif res['status'] == 'skip': stats['skipped'] += 1
        else: stats['failed'] += 1

    print("-" * 60)
    print(f"Total: {stats['total']} | Passed: {colors.GREEN}{stats['passed']}{colors.NC} | Failed: {colors.RED}{stats['failed']}{colors.NC} | Skipped: {colors.YELLOW}{stats['skipped']}{colors.NC}")
    
    return stats

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--type', choices=['type1', 'type2', 'type3'])
    parser.add_argument('--verbose', '-v', action='store_true')
    parser.add_argument('--benchmark', '-b', action='store_true')
    args = parser.parse_args()
    
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    
    clean_outputs()
    if not compile_java(): sys.exit(1)
    
    stats = run_tests(args.type, args.verbose, args.benchmark)
    # Bash scriptlerde exit code 0 başarıdır
    sys.exit(1 if stats['failed'] > 0 else 0)

def clean_outputs():
    try:
        for f in glob.glob(os.path.join(SRC_DIR, "*.class")): os.remove(f)
        for f in glob.glob(os.path.join(OUTPUT_DIR, "*.txt")): os.remove(f)
    except: pass

if __name__ == '__main__':
    main()