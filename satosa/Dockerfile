FROM satosa:8-alpine

USER root

RUN apk upgrade
RUN pip install gunicorn_worker_healthcheck

COPY files/usr/local/bin/docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
COPY files/usr/local/bin/gunicorn.conf.py /usr/local/bin/gunicorn.conf.py

EXPOSE 8080
EXPOSE 8081

RUN chown satosa:satosa /usr/local/bin/gunicorn.conf.py

USER satosa:satosa

CMD ["gunicorn", "-c/usr/local/bin/gunicorn.conf.py", "-w 4", "-b0.0.0.0:8080", "satosa.wsgi:app"]
