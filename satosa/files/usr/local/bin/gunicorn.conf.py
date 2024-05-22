#!/usr/local/bin/python

import gunicorn_worker_healthcheck

healthcheck_bind = '0.0.0.0:8081'
gunicorn_worker_healthcheck.start(globals())