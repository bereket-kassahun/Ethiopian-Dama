year=`date --utc +%Y` 
year="$(($year-1))"
date=`date --utc +%d`
month=`date --utc +%m`
month="$(($month+4))"
rest=`date --utc +%H:%M:%S%z`
date="$(($date+7))"
echo $year-0$month-$date'T'$rest