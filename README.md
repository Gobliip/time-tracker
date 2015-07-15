### Docker Container Config

#### Container links

| Container Name | Description                                                 |
| -------------- | ----------------------------------------------------------- |
| authweb        | Link to the [auth-server] authentication provider container |
| eureka         | Link to the [dominator] service discovery container         |
| timedb         | Link to the mysql database container                        |

#### Environment Variables

| Environment Variable | Description                |
| -------------------- | -------------------------- |
| MYSQL_DATABASE       | Name of the mysql database |
| MYSQL_USER           | User for the mysql conn    |
| MYSQL_ROOT_PASSWORD  | User password for mysql    |


[dominator]: https://github.com/Gobliip/dominator
[auth-server]: https://github.com/Gobliip/auth-server
