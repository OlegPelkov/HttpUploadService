# HttpUploadService
HttpUploadService

Implement HTTP service for file upload. Service supports parallel upload of multiple files.
File name is defined with custom HTTP header X-Upload-File:
Server supports parallel upload up to 50 files each of size 50 MB

## Upload
```
POST /api/v1/upload HTTP/1.1
Host: localhost:9090
X-Upload-File: test.zip
Content-Length: 1024
<bytes >
```

## Status
For each file count transfered bytes. Expose data on HTTP endpoint only for active uploads:

```
GET /api/v1/upload/progress HTTP/1.1
Host: localhost:9090
```

For each file observe upload duration. Expose data on HTTP endpoint:

```
GET /api/v1/upload/duration
Host: localhost:9090
```

## Build
For build use 
```
mvn package
```

## Start
For start service use 
```
java -jar HttpUploadService.jar
```


