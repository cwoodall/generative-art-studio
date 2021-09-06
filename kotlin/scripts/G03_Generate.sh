seq 1 1 100 | parallel --jobs=$1 ./gradlew sketch -Ptitle=g03.G03_TreesAndRoots --args=\"-n 1 -s {} -q -w 1920 -e 1080\"
