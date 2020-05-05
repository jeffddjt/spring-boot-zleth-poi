# Export Java Object to Excel 

## Step 1. add dependency to project
### maven 

    <dependency> 
        <groupId>com.zleth.poi</groupId> 
        <artifactId>spring-boot-zleth-poi</artifactId>
        <version>1.0.2.RELEASE</version>
    </dependency>

### gradle

    compile group: 'com.zleth.poi', name: 'spring-boot-zleth-poi', version: '1.0.2.RELEASE'

## Step 2. add annotation to Java Class

    Annotation ExcelAlias can add on class and properties.
        It means worksheet title when it annotated on class.
        It means column title when it annotated on property.
        
    Annotation ExcelIgnore can ignore export this property.
        It means will disappear this colmun in the excel file.
    For example:
    
        @ExcelAlias("user") //worksheet title
        pubblic class UserInfo {
            
            @ExcelAlias("User Identity") //column title
            private String id;
            
            @ExcelAlias("User Name") //column title
            private String username;
            
            @ExcelIgnore //disappear this column
            private String password;
            
            ......//getters and setters
        }
        
    Of course you add nothing annotation on the class then it will use classname and property name to generate worksheet title or column title.
    
## Step 3.  Use ExcelStream export excel file in Controller

    @RestController
    pubic class SomeController{
    
        @GetMapping("export")
        public Object exportExcel(){
            try{
                List<UserInfo> list = this.userInfoService.getAll();
                ExcelStream stream = new ExcelStream(UserInfo.class);
                ExcelTable table = stream.getTable(list);
                byte[] buf = stream.toExcel(table);
    
                org.springframework.core.io.Resource resource = new ByteArrayResource(buf);
                String contentType = "application/octet-stream";
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"ExportExcel.xls\"")
                        .body(resource);
            }catch (Exception e){
                log.error("",e);
                return ResponseEntity.status(400).body(e.getMessage());
            }
        }
        
    
    }

