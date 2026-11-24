# PowerShell script for RabbitMQ setup on Windows
$ErrorActionPreference = "Continue"

Write-Host "Enabling RabbitMQ management plugin..." -ForegroundColor Cyan
& "C:\Program Files\RabbitMQ Server\rabbitmq_server-4.4.0\sbin\rabbitmq-plugins.bat" enable rabbitmq_management

Write-Host "Configuring vhosts..." -ForegroundColor Cyan
& "C:\Program Files\RabbitMQ Server\rabbitmq_server-4.4.0\sbin\rabbitmqctl.bat" add_vhost scm360
& "C:\Program Files\RabbitMQ Server\rabbitmq_server-4.4.0\sbin\rabbitmqctl.bat" add_vhost scm360_test

$services = @("identity", "catalog", "supplier", "procurement", "inventory", "warehouse", "order", "transport", "analytics", "notification")

foreach ($svc in $services) {
    $username = "${svc}_svc"
    $password = "${svc}_mq_secret"
    Write-Host "Creating user $username..." -ForegroundColor Green
    & "C:\Program Files\RabbitMQ Server\rabbitmq_server-4.4.0\sbin\rabbitmqctl.bat" add_user $username $password
    & "C:\Program Files\RabbitMQ Server\rabbitmq_server-4.4.0\sbin\rabbitmqctl.bat" set_user_tags $username management
    & "C:\Program Files\RabbitMQ Server\rabbitmq_server-4.4.0\sbin\rabbitmqctl.bat" set_permissions -p scm360 $username ".*" ".*" ".*"
    & "C:\Program Files\RabbitMQ Server\rabbitmq_server-4.4.0\sbin\rabbitmqctl.bat" set_permissions -p scm360_test $username ".*" ".*" ".*"
}

& "C:\Program Files\RabbitMQ Server\rabbitmq_server-4.4.0\sbin\rabbitmqctl.bat" set_permissions -p scm360 guest ".*" ".*" ".*"
& "C:\Program Files\RabbitMQ Server\rabbitmq_server-4.4.0\sbin\rabbitmqctl.bat" set_permissions -p scm360_test guest ".*" ".*" ".*"

Write-Host "RabbitMQ setup finished." -ForegroundColor Green
