# 基础镜像使用java
FROM java:8
# VOLUME 指定临时文件目录为/tmp
# 其效果是在主机 /var/lib/docker 目录下创建一个临时文件，并链接到容器的 /tmp
VOLUME /tmp
# 将jar包添加到容器中并更名为 mydocker.jar
ADD vueblog-0.0.1-SNAPSHOT.jar mydocker.jar
# 运行jar包
RUN bash -c 'touch /mydocker.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/mydocker.jar"]