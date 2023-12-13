                                                                                                                    

import subprocess
import time
import random
import sys

def execute_wget(target_ip):
    command = "wget -O - " + target_ip + " > /dev/null  2>&1"
    # Execute wget command
    subprocess.call(command, shell=True)

def main():
    if len(sys.argv) != 3:
        print("Usage: python script.py <target_ip> <traffic_intensity>")
        sys.exit(1)

    target_ip = sys.argv[1]
    mean_interval = float(sys.argv[2])

    while True:
        # Sleep for a random duration based on Poisson distribution
        sleep_time = random.expovariate(1 / mean_interval)
        time.sleep(sleep_time)

        # Execute wget command with the specified target IP
        execute_wget(target_ip)

if __name__ == "__main__":
    main()

