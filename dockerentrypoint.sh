#!/bin/bash
export LD_LIBRARY_PATH=/usr/local/lib:$LD_LIBRARY_PATH
export PMIX_MCA_pcompress_base_silence_warning=1
cd /app && make all
