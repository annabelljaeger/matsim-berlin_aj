#!/bin/bash --login
#SBATCH --job-name=test
#SBATCH --output=test.log
#SBATCH --nodes=1                          # Run all cores on a single node
#SBATCH --ntasks=1                         # Run a single task        
#SBATCH --cpus-per-task=16                 # Number of CPU cores per task
#SBATCH --mem=120G                          # Job memory request
#SBATCH --time=36:00:00                    # Time limit hrs:min:sec
#SBATCH --mail-type=BEGIN,END,FAIL         # Send email on begin, end, and fail
#SBATCH --mail-user=a.jaeger@campus.tu-berlin.de       # Your email address
# load your java version
module load java/21
# execute a command
java -Xmx120G -cp ./matsim-berlin-6.1-af58399-dirty.jar org.matsim.run.OpenBerlinScenario run --config=input/berlin-v6.1.config_Maut_cluster.xml
