seq 1 1 $2 | parallel --jobs=$1 ./gradlew sketch -Ptitle=g02.G02_SpaceColonization_NodeSizeBasedOnChildCount --args=\"-n 1 -s {} -q -w 1920 -e 1080\"
