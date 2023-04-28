## Mini Amazons
Web App Mini Amazon is a simplified e-commerce website that provides a user-friendly and streamlined shopping experience. It allows users to search for and purchase products online using various filters, view product descriptions, images, and user reviews, add items to their cart, and checkout. The application typically includes shipping and delivery options and allows customers to view their order history and track the status of their orders. 

### Dependency
- Spring 3
- React
- protoc compiler version protoc-22.3-osx-x86_64


### Required Functions
- Allow user to buy desired items, with specified address and quantity
- Query placed order, check status, track id, as well as other informations of the order
- Implemented categories, user can search by the name of the category
- Send user specified upsName to ups


### Extra Features

- [x] Always find the closest warehouse location to destination
- [x] SOA (Service-Oriented Architecture)
- [x] Pagination and sorting
- [x] At most once semantics
- [x] mini message queue for async acknowledgements
- [x] email notification
- [x] login and verification using Jwt in spring security
- [x] responsive design
- [x] shopping cart
- [x] remove from shopping cart
- edit shipment


### Danger Log

- When the frontend and backend of a web application are running on different ports, it can lead to a Cross-Origin Resource Sharing (CORS) error. This error occurs when the browser tries to access resources from a different domain, port, or protocol than the one it originated from. CORS errors can be a serious security risk, as they can allow unauthorized access to sensitive data and resources.

- When designing JPA models with relationships and cascade types, it is crucial to carefully consider the impact of these design decisions on the application's functionality and performance. Poor JPA model design can lead to coding errors, inconsistencies in data, loss of data, unintended deletion or modification of related entities, and data integrity issues. To prevent these issues, developers must follow best practices, including defining relationships between entities and carefully selecting cascade types based on the application's requirements. Proper testing, database schema design, and indexing are also critical to ensure optimal performance. By carefully planning and designing the JPA model and testing it thoroughly, developers can ensure that the application works as intended and avoid potential issues that can arise from poor design choices.

- When the backend throw error to return an response with an error message to the front end, it's necessaryt to return the corresponding error message. Otherwise, it will be very difficult to debug. For example, in controller, if we try to catch all kinds of the error, and return a response to the front end with the same error message no matter what kind of error was throwed, the message returned to the front end will be very misleading, and could cause more and more time wasted to resolve the issue. A better implementation is define a exception class (we call it ServiceExceptionHandler), it can catch the error and throw a bad_request to the front end with appropriate error message.  

- Instead of directly storing the password of the user in the database, we should at least add a salt to that password and hash it to ensure security. A better approach is just to use PasswordEncoder.encode, which is provided by spring boost security.

- When we acquire information of the logged user from context, the user we got from (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal() is not really the User we should be refering to. Because in that object, we might have a user that missing a few fields. What we should do is use the user_id of that user to find the real user stored in the database. 

- We should use useEffect in the front end when we want to render something, useEffect will only render the component when a value changes, and it can save resource of the system by avoid render the same component over and over again. 

- when we send address X, and Y to the backend, we should use parseInt(X) and parseInt(Y) in the body of the request, otherwise front end will send X and Y as string, and receive a 400 bad request, since in the back end we specify X and Y to be int.

- It is necessary to check the user entered value, for example, quantity should not be negative or 0. We will do that check in back end, when back end receive a invalid request, it will return a response, and front end will alert the user with an error message.

- In the back end, under the server socket folder, these is a UPS with minium functionality. We only used it for testing purposes to make sure our amazon can communicate with UPS as expected. 
