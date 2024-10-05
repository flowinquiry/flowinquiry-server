INSERT
    INTO
        fw_crm_account(
            id,
            name,
            TYPE,
            industry,
            website,
            phone_number,
            email,
            address_line1,
            address_line2,
            city,
            state,
            postal_code,
            country,
            annual_revenue,
            parent_account_id,
            created_at,
            updated_at,
            status,
            assigned_to_user_id,
            notes
        )
    VALUES(
        1000,
        'account_1',
        'Reseller',
        'Information Technology',
        NULL,
        '111-111-1111',
        NULL,
        'Address',
        NULL,
        'City',
        NULL,
        '11111',
        NULL,
        NULL,
        NULL,
        '2024-09-29 01:31:22.185824',
        '2024-09-29 01:31:22.185938',
        'Active',
        NULL,
        NULL
    );

ALTER SEQUENCE fw_crm_account_id_seq RESTART WITH 2000;