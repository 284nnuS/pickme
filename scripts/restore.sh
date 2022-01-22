cat backup.sql | docker exec -i pickme-database /usr/bin/mysql -u root --password=$PASS DATABASE
