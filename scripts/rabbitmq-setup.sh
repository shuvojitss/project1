#!/usr/bin/env bash
# =============================================================================
# Infy_SupplyChain360 - RabbitMQ Setup Script
# Configures vhosts, plugins, and dedicated service accounts
# =============================================================================

set -e

echo "Enabling RabbitMQ management plugin..."
rabbitmq-plugins enable rabbitmq_management

echo "Creating vhosts..."
rabbitmqctl add_vhost scm360 || true
rabbitmqctl add_vhost scm360_test || true

SERVICES=("identity" "catalog" "supplier" "procurement" "inventory" "warehouse" "order" "transport" "analytics" "notification")

for svc in "${SERVICES[@]}"; do
    username="${svc}_svc"
    password="${svc}_mq_secret"
    echo "Creating user $username..."
    rabbitmqctl add_user "$username" "$password" || rabbitmqctl change_password "$username" "$password"
    rabbitmqctl set_user_tags "$username" management
    
    # Set permissions on production vhost
    rabbitmqctl set_permissions -p scm360 "$username" ".*" ".*" ".*"
    # Set permissions on test vhost
    rabbitmqctl set_permissions -p scm360_test "$username" ".*" ".*" ".*"
done

# Ensure guest admin user is available on scm360 for dev/local inspection
rabbitmqctl set_permissions -p scm360 guest ".*" ".*" ".*" || true
rabbitmqctl set_permissions -p scm360_test guest ".*" ".*" ".*" || true

echo "RabbitMQ setup completed successfully."
