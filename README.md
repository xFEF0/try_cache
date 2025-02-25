# Cache with Spring Boot
# Run
Before:

`docker pull postgres`

`docker run --name postgresql -e POSTGRES_DB=dataz -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin123 -p 5432:5432 -d postgres`

`docker pull redis`

`docker run -d --name my-redis -p 6379:6379 redis`

Start the Spring Boot app