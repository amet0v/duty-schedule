# REST API приложение для контроля дежурств сотрудников

Документация swagger - `localhost:8080/swagger-ui/index.html`

После запуска приложения необходимо создать базового пользователя:\
`GET /api/v1/not-secured/init`\
логин/пароль задаются в application.properties

Заполнить БД отделами(departments):\
`POST /api/v1/not-secured/departments/fill`\
работает только при пустой таблице отделов, не требует авторизации

Получить дежурного в текущий день\
`GET /api/v1/schedule/duty/{departmentId}`
