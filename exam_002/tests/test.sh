#!/bin/bash
set -u

mkdir -p /logs/verifier /tmp/reports
REPORT_DIR=/tmp/reports
SCORE=0
TOTAL=6

cd /app
if ! bash build.sh > /tmp/build.log 2>&1; then
    echo "FAIL: build"
    echo "0.00" > /logs/verifier/reward.txt
    exit 0
fi

assert_field() {
    local scene=$1 key=$2 want=$3
    local got
    got=$(awk -F= -v k="$key" '$1==k{print substr($0, length(k)+2); exit}' "$REPORT_DIR/$scene.txt")
    if [ "$got" != "$want" ]; then
        echo "MISMATCH scene=$scene field=$key"
        return 1
    fi
    return 0
}

run_scene() {
    local scene=$1 pstart=$2 pend=$3 net=$4 rev=$5 closed=$6 err=$7
    local report=$REPORT_DIR/$scene.txt
    if ! java -cp /app/out com.settle.Main /app/data/ledgers/$scene.txt $scene > $report 2>/dev/null; then
        echo "FAIL: scene=$scene crashed"
        return 1
    fi
    local ok=0
    assert_field $scene SCENE "$scene" || ok=1
    assert_field $scene PERIOD_START "$pstart" || ok=1
    assert_field $scene PERIOD_END "$pend" || ok=1
    assert_field $scene NET_POINTS "$net" || ok=1
    assert_field $scene REVERSALS "$rev" || ok=1
    assert_field $scene CLOSED_ORDERS "$closed" || ok=1
    assert_field $scene ERROR_LINES "$err" || ok=1
    return $ok
}

run_scene scene1 "2026-03-01 00:00:00" "2026-03-31 23:59:59" 60 0 0 0 && SCORE=$((SCORE+1))
run_scene scene2 "2026-04-01 00:00:00" "2026-04-30 23:59:59" 40 1 0 0 && SCORE=$((SCORE+1))
run_scene scene3 "2026-05-01 00:00:00" "2026-05-31 23:59:59" 0 1 1 1 && SCORE=$((SCORE+1))
run_scene scene4 "2026-06-01 00:00:00" "2026-06-30 23:59:59" 0 1 1 0 && SCORE=$((SCORE+1))
run_scene scene5 "2026-07-01 00:00:00" "2026-07-31 23:59:59" 80 0 0 2 && SCORE=$((SCORE+1))
run_scene scene6 "2026-08-01 00:00:00" "2026-08-31 23:59:59" 0 1 1 1 && SCORE=$((SCORE+1))

echo "Score: $SCORE / $TOTAL"
awk -v s=$SCORE -v t=$TOTAL 'BEGIN{printf "%.2f", s/t}' > /logs/verifier/reward.txt
cat /logs/verifier/reward.txt
