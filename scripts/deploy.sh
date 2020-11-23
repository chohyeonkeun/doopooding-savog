#!/bin/bash

REPOSITORY=/home/ec2-user/app/step2
PROJECT_NAME=savog

echo "> nginx 설치"
sudo yum install nginx

echo "> nginx 버젼 확인"
nginx -v

echo "> nginx 설정파일을 /etc/nginx/conf.d/default.conf/로 카피"
cp $REPOSITORY/zip/frontend/management/nginx.conf /etc/nginx/conf.d/default.conf

echo "> cd $REPOSITORY/zip/frontend/management/"
cd $REPOSITORY/zip/frontend/managmenet

echo "> npm run build"
npm run build

echo "> dist 파일을 /usr/share/nginx/html/로 카피"
cp $REPOSITORY/zip/frontend/management/dist /usr/share/nginx/html

echo "> nginx 문법 체크"
sudo nginx -t

echo "> nginx 재시작"
sudo systemctl restart nginx

echo "> Build 파일복사"
cp $REPOSITORY/zip/backend/*.jar $REPOSITORY/

JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo "> JAR NAME: $JAR_NAME"

echo "> 현재 구동 중인 애플리케이션 pid 확인"

CURRENT_PID=$(pgrep -f $JAR_NAME)

echo "현재 구동 중인 애플리케이션 pid : $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
  echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> 새 애플리케이션 배포"

echo "> JAR_NAME 에 실행권한 추가"

chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"

nohup java -jar \
  -Dspring.config.location=/home/ec2-user/app/application.yaml \
  -Dspring.profiles.active=real \
  $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &
