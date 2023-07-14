# java-course

1. docker compose build
2. docker compose up
3. wait for all services to be up (the log agent waits until the web api and griddb server are up before it starts
4. ssh into log agent container ( $ docker exec -it log-agent /bin/bash)
5. run test: $ ./tests/testAgent.sh
    - This test will write to a text fiel every .5 seconds and the log reader will read that and write to griddb
6. Exit container
7. from host machine, run the griddb test
    - $ ./agent/tests/readGridDBContainer.sh
        - you should see the rows of data printed out and then the container deleted

