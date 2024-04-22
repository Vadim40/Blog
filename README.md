# Blog
This is a REST API for a leading blog. It provides the ability to create accounts, comments, and articles. You also have the option to follow  topics or users.
### Entities
1)User  
2)Aritcle  
3)Comment  
4)Image  
5)Topic  
  
The diagram below describes the main entities and provides additional tables for functionalities like likes and following. The database schema is in 3rd Normal Form and uses table bundles to prevent many-to-many relationships.  

![image](https://github.com/Vadim40/Blog/assets/117930997/19056c2c-61d3-4436-8f9b-888c645f280f)  

### Services
Created custom exceptions.    
![image](https://github.com/Vadim40/Blog/assets/117930997/df28b3d1-750a-48c9-b875-0dd4d30185f1)  
Most methods return a Page.  


### Controlles  
Here is used Restcontroller. Most methods retrieve a Page with a model from services, then they map entities to DTOs for various situations. A GlobalExceptionHandler returns an exception message and HTTP status. It handles custom exceptions and some others.

### Security
Roles are used to grant permissions for actions, and JWT tokens are used for authentication. There is a filter by URL and roles.  
![image](https://github.com/Vadim40/Blog/assets/117930997/f6252392-aed1-4ae4-81f1-1df5cae4a8e3)

### Testing 
Made junit tests for service and repository layers.
