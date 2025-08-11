CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type VARCHAR(50) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(500),
    content TEXT,
    template_name VARCHAR(100),
    user_id BIGINT,
    retry_count INTEGER NOT NULL DEFAULT 0,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP,
    delivered_at TIMESTAMP
);

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_notifications_type ON notifications(type);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);
CREATE INDEX idx_notifications_recipient ON notifications(recipient);

CREATE TABLE notification_metadata (
    notification_id UUID NOT NULL,
    metadata_key VARCHAR(100) NOT NULL,
    metadata_value TEXT,
    PRIMARY KEY (notification_id, metadata_key),
    FOREIGN KEY (notification_id) REFERENCES notifications(id) ON DELETE CASCADE
);

CREATE INDEX idx_notification_metadata_key ON notification_metadata(metadata_key); 