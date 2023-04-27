## Mini Amazons
Web App Mini Amazon is a simplified e-commerce website that provides a user-friendly and streamlined shopping experience. It allows users to search for and purchase products online using various filters, view product descriptions, images, and user reviews, add items to their cart, and checkout. The application typically includes shipping and delivery options and allows customers to view their order history and track the status of their orders. 

### Dependency
- Spring 3
- React
- protoc compiler version protoc-22.3-osx-x86_64

### Features

- [x] Always find the closest warehouse location to destination
- [x] SOA (Service-Oriented Architecture)
- [x] Pagination and sorting
- [x] At most once semantics
- [x] mini message queue for async acknowledgements
- [x] email notification
- [x] login and verification using Jwt in spring security
- [x] responsive html
- [x]shopping cart

### Danger Log

- When the frontend and backend of a web application are running on different ports, it can lead to a Cross-Origin Resource Sharing (CORS) error. This error occurs when the browser tries to access resources from a different domain, port, or protocol than the one it originated from. CORS errors can be a serious security risk, as they can allow unauthorized access to sensitive data and resources.
- When designing JPA models with relationships and cascade types, it is crucial to carefully consider the impact of these design decisions on the application's functionality and performance. Poor JPA model design can lead to coding errors, inconsistencies in data, loss of data, unintended deletion or modification of related entities, and data integrity issues. To prevent these issues, developers must follow best practices, including defining relationships between entities and carefully selecting cascade types based on the application's requirements. Proper testing, database schema design, and indexing are also critical to ensure optimal performance. By carefully planning and designing the JPA model and testing it thoroughly, developers can ensure that the application works as intended and avoid potential issues that can arise from poor design choices.
- When the backend throw error to return an response with an error message to the front end, it's necessaryt to return the corresponding error message. Otherwise, it will be very difficult to debug. For example, in controller, if we try to catch all kinds of the error, and return a response to the front end with the same error message no matter what kind of error was throwed, the message returned to the front end will be very misleading, and could cause more and more time wasted to resolve the issue. A better implementation is define a exception class (we call it ServiceExceptionHandler), it can catch the error and throw a bad_request to the front end with appropriate error message.  

### To Do
- send upsName to ups
- [x]remove from shopping cart
- edit shipment
- [x]issue with query order
- fix auto increment by 50
