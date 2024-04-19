# Install
- Install `openjdk-17-jre` and `openjdk-17-jdk` using apt
- Download the source code (`.tar.gz` file) of the OpenMPI at the homepage
- Configure: `./configure --enable-mpi-java`
- Make: `make` and `make install`
- Add environment variable: `export LD_LIBRARY_PATH=/usr/local/lib`
# Code and run
- For coding in VSCode: install the Java extension, choose `Java Projects` at the sidebar, choose `Referenced Libraries`, and choose the location of the `mpi.jar` file (`/usr/local/lib/mpi.jar`)
- Compile and run: Using `make`