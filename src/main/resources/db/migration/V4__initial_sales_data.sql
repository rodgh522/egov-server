-- =====================================================
-- CRM Project - Initial Sales Module Data
-- Version: V4
-- Description: Seeds initial data for Sales Management module
-- Author: CRM Project Team
-- Date: 2026-01-29
-- =====================================================

-- =====================================================
-- 1. PIPELINE STAGES (Sales Pipeline)
-- =====================================================

INSERT INTO PIPELINE_STAGES (STAGE_ID, STAGE_NAME, STAGE_CODE, STAGE_ORDER, PROBABILITY, STAGE_COLOR, IS_WON, IS_LOST, TENANT_ID, USE_AT, CREATED_DATE)
VALUES
('STG001', 'Prospecting', 'PROSPECT', 1, 10, '#6B7280', 'N', 'N', 'SYSTEM', 'Y', CURRENT_TIMESTAMP),
('STG002', 'Qualification', 'QUALIFY', 2, 25, '#3B82F6', 'N', 'N', 'SYSTEM', 'Y', CURRENT_TIMESTAMP),
('STG003', 'Proposal', 'PROPOSAL', 3, 50, '#F59E0B', 'N', 'N', 'SYSTEM', 'Y', CURRENT_TIMESTAMP),
('STG004', 'Negotiation', 'NEGOTIATE', 4, 75, '#8B5CF6', 'N', 'N', 'SYSTEM', 'Y', CURRENT_TIMESTAMP),
('STG005', 'Closed Won', 'CLOSED_WON', 5, 100, '#10B981', 'Y', 'N', 'SYSTEM', 'Y', CURRENT_TIMESTAMP);

-- =====================================================
-- 2. PRODUCTS
-- =====================================================

INSERT INTO PRODUCTS (PRODUCT_ID, PRODUCT_CODE, PRODUCT_NAME, PRODUCT_CATEGORY, PRODUCT_TYPE, DESCRIPTION, UNIT_PRICE, COST_PRICE, CURRENCY, TAX_RATE, STOCK_QUANTITY, IS_ACTIVE, TENANT_ID, USE_AT, CREATED_DATE, CREATED_BY)
VALUES
('PRD001', 'SW-CRM-ENT', 'CRM Enterprise License', 'SOFTWARE', 'PRODUCT', 'Enterprise CRM software license - annual subscription', 12000000.00, 3000000.00, 'KRW', 10.00, 999, 'Y', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('PRD002', 'SW-CRM-STD', 'CRM Standard License', 'SOFTWARE', 'PRODUCT', 'Standard CRM software license - annual subscription', 6000000.00, 1500000.00, 'KRW', 10.00, 999, 'Y', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('PRD003', 'SVC-IMPL', 'Implementation Service', 'SERVICE', 'SERVICE', 'CRM implementation and customization service', 5000000.00, 2000000.00, 'KRW', 10.00, 100, 'Y', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('PRD004', 'SVC-TRAIN', 'Training Service', 'SERVICE', 'SERVICE', 'User training and onboarding service (per day)', 1000000.00, 400000.00, 'KRW', 10.00, 200, 'Y', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('PRD005', 'SVC-SUPPORT', 'Premium Support', 'SERVICE', 'SERVICE', 'Premium support package - annual subscription', 3000000.00, 1000000.00, 'KRW', 10.00, 100, 'Y', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001');

-- =====================================================
-- 3. CUSTOMERS
-- =====================================================

INSERT INTO CUSTOMERS (CUSTOMER_ID, CUSTOMER_NAME, CUSTOMER_CODE, CUSTOMER_TYPE, INDUSTRY, COMPANY_SIZE, WEBSITE, PHONE, EMAIL, ADDRESS, ANNUAL_REVENUE, EMPLOYEE_COUNT, ASSIGNED_USER_ID, BRANCH_ID, TENANT_ID, USE_AT, CREATED_DATE, CREATED_BY)
VALUES
('CUS001', 'TechCorp Solutions', 'TC-001', 'COMPANY', 'Technology', 'MEDIUM', 'https://techcorp.example.com', '02-1234-5678', 'contact@techcorp.example.com', 'Seoul, Gangnam-gu, Teheran-ro 123', 50000000000.00, 250, 'SYS_USER001', 'SYS_BR001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('CUS002', 'Global Manufacturing Inc', 'GM-001', 'COMPANY', 'Manufacturing', 'LARGE', 'https://globalmanuf.example.com', '031-9876-5432', 'sales@globalmanuf.example.com', 'Gyeonggi-do, Suwon-si, Yeongtong-gu 456', 120000000000.00, 1500, 'SYS_USER001', 'SYS_BR001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('CUS003', 'Healthcare Partners', 'HP-001', 'COMPANY', 'Healthcare', 'MEDIUM', 'https://healthpartners.example.com', '02-5555-1234', 'info@healthpartners.example.com', 'Seoul, Jongno-gu, Jongno 789', 30000000000.00, 180, 'SYS_USER001', 'SYS_BR001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('CUS004', 'FinanceFirst Bank', 'FF-001', 'COMPANY', 'Finance', 'LARGE', 'https://financefirst.example.com', '02-8888-9999', 'corporate@financefirst.example.com', 'Seoul, Jung-gu, Myeongdong 101', 500000000000.00, 3000, 'SYS_USER001', 'SYS_BR001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('CUS005', 'EduTech Academy', 'ET-001', 'COMPANY', 'Education', 'SMALL', 'https://edutech.example.com', '02-3333-4444', 'hello@edutech.example.com', 'Seoul, Mapo-gu, Hongdae 202', 5000000000.00, 45, 'SYS_USER001', 'SYS_BR001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001');

-- =====================================================
-- 4. CONTACTS
-- =====================================================

INSERT INTO CONTACTS (CONTACT_ID, CUSTOMER_ID, CONTACT_NAME, CONTACT_TITLE, DEPARTMENT, PHONE, MOBILE, EMAIL, IS_PRIMARY, TENANT_ID, USE_AT, CREATED_DATE, CREATED_BY)
VALUES
('CON001', 'CUS001', 'Kim Min-jun', 'CTO', 'IT Department', '02-1234-5678', '010-1111-2222', 'minjun.kim@techcorp.example.com', 'Y', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('CON002', 'CUS002', 'Lee Soo-yeon', 'Procurement Manager', 'Purchasing', '031-9876-5432', '010-3333-4444', 'sooyeon.lee@globalmanuf.example.com', 'Y', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('CON003', 'CUS003', 'Park Ji-hoon', 'IT Director', 'Information Systems', '02-5555-1234', '010-5555-6666', 'jihoon.park@healthpartners.example.com', 'Y', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('CON004', 'CUS004', 'Choi Yuna', 'VP of Operations', 'Operations', '02-8888-9999', '010-7777-8888', 'yuna.choi@financefirst.example.com', 'Y', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('CON005', 'CUS005', 'Jung Dae-ho', 'CEO', 'Executive', '02-3333-4444', '010-9999-0000', 'daeho.jung@edutech.example.com', 'Y', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001');

-- =====================================================
-- 5. LEADS
-- =====================================================

INSERT INTO LEADS (LEAD_ID, LEAD_NAME, COMPANY_NAME, CONTACT_NAME, EMAIL, PHONE, LEAD_SOURCE, LEAD_STATUS, LEAD_SCORE, INDUSTRY, ESTIMATED_REVENUE, DESCRIPTION, ASSIGNED_USER_ID, TENANT_ID, USE_AT, CREATED_DATE, CREATED_BY)
VALUES
('LED001', 'SmartRetail Expansion Project', 'SmartRetail Co', 'Han Seung-woo', 'seungwoo@smartretail.example.com', '02-1111-2222', 'WEBSITE', 'NEW', 30, 'Retail', 15000000.00, 'Interested in CRM for retail chain management', 'SYS_USER001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('LED002', 'LogiTrans Digital Transformation', 'LogiTrans Inc', 'Yoo Jin-ae', 'jinae@logitrans.example.com', '032-3333-4444', 'REFERRAL', 'CONTACTED', 45, 'Logistics', 25000000.00, 'Looking for integrated sales and logistics solution', 'SYS_USER001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('LED003', 'MediaGroup CRM Migration', 'MediaGroup Corp', 'Shin Hye-jin', 'hyejin@mediagroup.example.com', '02-5555-6666', 'TRADE_SHOW', 'QUALIFIED', 65, 'Media', 35000000.00, 'Migrating from legacy CRM system', 'SYS_USER001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('LED004', 'BioTech Research CRM', 'BioTech Labs', 'Kang Min-seo', 'minseo@biotech.example.com', '042-7777-8888', 'COLD_CALL', 'NEW', 20, 'Biotechnology', 20000000.00, 'Early stage inquiry for research team collaboration', 'SYS_USER001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('LED005', 'FoodService Chain CRM', 'TastyChain Inc', 'Oh Sung-min', 'sungmin@tastychain.example.com', '02-9999-0000', 'PARTNER', 'CONTACTED', 55, 'Food Service', 18000000.00, 'Franchise management and customer loyalty system', 'SYS_USER001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001');

-- =====================================================
-- 6. OPPORTUNITIES
-- =====================================================

INSERT INTO OPPORTUNITIES (OPPORTUNITY_ID, OPPORTUNITY_NAME, CUSTOMER_ID, CONTACT_ID, STAGE_ID, AMOUNT, PROBABILITY, EXPECTED_CLOSE_DATE, LEAD_SOURCE, DESCRIPTION, NEXT_STEP, ASSIGNED_USER_ID, BRANCH_ID, TENANT_ID, USE_AT, CREATED_DATE, CREATED_BY)
VALUES
('OPP001', 'TechCorp CRM Enterprise Deal', 'CUS001', 'CON001', 'STG003', 25000000.00, 50, '2026-03-31', 'WEBSITE', 'Enterprise CRM deployment for entire organization', 'Schedule product demo with IT team', 'SYS_USER001', 'SYS_BR001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('OPP002', 'Global Manufacturing System Upgrade', 'CUS002', 'CON002', 'STG004', 45000000.00, 75, '2026-02-28', 'REFERRAL', 'Full CRM suite with manufacturing integration', 'Final contract negotiation', 'SYS_USER001', 'SYS_BR001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('OPP003', 'Healthcare Partners Pilot', 'CUS003', 'CON003', 'STG002', 15000000.00, 25, '2026-04-30', 'TRADE_SHOW', 'Pilot project for patient management CRM', 'Technical requirements gathering', 'SYS_USER001', 'SYS_BR001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('OPP004', 'FinanceFirst Enterprise Rollout', 'CUS004', 'CON004', 'STG001', 80000000.00, 10, '2026-06-30', 'COLD_CALL', 'Bank-wide CRM implementation project', 'Initial discovery meeting', 'SYS_USER001', 'SYS_BR001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('OPP005', 'EduTech Standard Package', 'CUS005', 'CON005', 'STG005', 8000000.00, 100, '2026-01-15', 'PARTNER', 'Standard CRM for education management', 'Contract signed - proceed to implementation', 'SYS_USER001', 'SYS_BR001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001');

-- =====================================================
-- 7. OPPORTUNITY PRODUCTS
-- =====================================================

INSERT INTO OPPORTUNITY_PRODUCTS (OPPORTUNITY_ID, PRODUCT_ID, QUANTITY, UNIT_PRICE, DISCOUNT_RATE, DISCOUNT_AMOUNT, TOTAL_AMOUNT, TENANT_ID, CREATED_DATE)
VALUES
('OPP001', 'PRD001', 1, 12000000.00, 0.00, 0.00, 12000000.00, 'SYSTEM', CURRENT_TIMESTAMP),
('OPP001', 'PRD003', 1, 5000000.00, 0.00, 0.00, 5000000.00, 'SYSTEM', CURRENT_TIMESTAMP),
('OPP002', 'PRD001', 3, 12000000.00, 5.00, 1800000.00, 34200000.00, 'SYSTEM', CURRENT_TIMESTAMP),
('OPP002', 'PRD004', 5, 1000000.00, 0.00, 0.00, 5000000.00, 'SYSTEM', CURRENT_TIMESTAMP),
('OPP005', 'PRD002', 1, 6000000.00, 10.00, 600000.00, 5400000.00, 'SYSTEM', CURRENT_TIMESTAMP);

-- =====================================================
-- 8. QUOTES
-- =====================================================

INSERT INTO QUOTES (QUOTE_ID, QUOTE_NUMBER, OPPORTUNITY_ID, CUSTOMER_ID, CONTACT_ID, QUOTE_STATUS, QUOTE_DATE, VALID_UNTIL, SUBTOTAL, DISCOUNT_AMOUNT, TAX_AMOUNT, TOTAL_AMOUNT, CURRENCY, PAYMENT_TERMS, DELIVERY_TERMS, NOTES, ASSIGNED_USER_ID, TENANT_ID, USE_AT, CREATED_DATE, CREATED_BY)
VALUES
('QUO001', 'Q-2026-0001', 'OPP001', 'CUS001', 'CON001', 'SENT', '2026-01-20', '2026-02-20', 17000000.00, 0.00, 1700000.00, 18700000.00, 'KRW', 'Net 30 days', 'Electronic delivery', 'Initial quote for CRM Enterprise package', 'SYS_USER001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('QUO002', 'Q-2026-0002', 'OPP002', 'CUS002', 'CON002', 'APPROVED', '2026-01-15', '2026-02-15', 39200000.00, 1800000.00, 3740000.00, 41140000.00, 'KRW', 'Net 45 days', 'On-site installation', 'Volume discount applied for 3 licenses', 'SYS_USER001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('QUO003', 'Q-2026-0003', 'OPP003', 'CUS003', 'CON003', 'DRAFT', '2026-01-28', '2026-02-28', 12000000.00, 0.00, 1200000.00, 13200000.00, 'KRW', 'Net 30 days', 'Electronic delivery', 'Pilot project quote - limited scope', 'SYS_USER001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('QUO004', 'Q-2026-0004', NULL, 'CUS004', 'CON004', 'DRAFT', '2026-01-29', '2026-03-01', 75000000.00, 5000000.00, 7000000.00, 77000000.00, 'KRW', 'Net 60 days', 'Phased deployment', 'Enterprise quote pending opportunity creation', 'SYS_USER001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('QUO005', 'Q-2026-0005', 'OPP005', 'CUS005', 'CON005', 'ACCEPTED', '2026-01-10', '2026-02-10', 5400000.00, 600000.00, 480000.00, 5280000.00, 'KRW', 'Net 15 days', 'Electronic delivery', 'Startup discount applied', 'SYS_USER001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001');

-- =====================================================
-- 9. QUOTE ITEMS
-- =====================================================

INSERT INTO QUOTE_ITEMS (QUOTE_ID, PRODUCT_ID, ITEM_ORDER, QUANTITY, UNIT_PRICE, DISCOUNT_RATE, DISCOUNT_AMOUNT, TAX_RATE, TAX_AMOUNT, TOTAL_AMOUNT, DESCRIPTION, TENANT_ID, CREATED_DATE)
VALUES
('QUO001', 'PRD001', 1, 1, 12000000.00, 0.00, 0.00, 10.00, 1200000.00, 13200000.00, 'CRM Enterprise License - Annual', 'SYSTEM', CURRENT_TIMESTAMP),
('QUO001', 'PRD003', 2, 1, 5000000.00, 0.00, 0.00, 10.00, 500000.00, 5500000.00, 'Implementation Service', 'SYSTEM', CURRENT_TIMESTAMP),
('QUO002', 'PRD001', 1, 3, 12000000.00, 5.00, 1800000.00, 10.00, 3240000.00, 35640000.00, 'CRM Enterprise License x3 - Volume discount', 'SYSTEM', CURRENT_TIMESTAMP),
('QUO002', 'PRD004', 2, 5, 1000000.00, 0.00, 0.00, 10.00, 500000.00, 5500000.00, 'Training Service - 5 days', 'SYSTEM', CURRENT_TIMESTAMP),
('QUO005', 'PRD002', 1, 1, 6000000.00, 10.00, 600000.00, 10.00, 540000.00, 5940000.00, 'CRM Standard License - Startup discount', 'SYSTEM', CURRENT_TIMESTAMP);

-- =====================================================
-- 10. ACTIVITIES
-- =====================================================

INSERT INTO ACTIVITIES (ACTIVITY_ID, ACTIVITY_TYPE, ACTIVITY_SUBJECT, ACTIVITY_DESCRIPTION, ACTIVITY_STATUS, ACTIVITY_DATE, DUE_DATE, DURATION_MINUTES, PRIORITY, RELATED_TYPE, RELATED_ID, ASSIGNED_USER_ID, TENANT_ID, USE_AT, CREATED_DATE, CREATED_BY)
VALUES
('ACT001', 'CALL', 'Follow-up call with TechCorp CTO', 'Discuss technical requirements and integration needs', 'COMPLETED', '2026-01-25 10:00:00', '2026-01-25 10:30:00', 30, 'HIGH', 'OPPORTUNITY', 'OPP001', 'SYS_USER001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('ACT002', 'MEETING', 'Contract negotiation meeting - Global Manufacturing', 'Final terms discussion with procurement team', 'PLANNED', '2026-02-05 14:00:00', '2026-02-05 16:00:00', 120, 'HIGH', 'OPPORTUNITY', 'OPP002', 'SYS_USER001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('ACT003', 'TASK', 'Prepare demo environment for Healthcare Partners', 'Set up sandbox with healthcare-specific configurations', 'IN_PROGRESS', '2026-01-28 09:00:00', '2026-02-01 18:00:00', 480, 'NORMAL', 'OPPORTUNITY', 'OPP003', 'SYS_USER001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('ACT004', 'EMAIL', 'Send introduction materials to FinanceFirst', 'Product brochure and case studies for banking sector', 'COMPLETED', '2026-01-20 11:00:00', '2026-01-20 11:30:00', 15, 'NORMAL', 'CUSTOMER', 'CUS004', 'SYS_USER001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001'),
('ACT005', 'NOTE', 'EduTech implementation kickoff notes', 'Documented requirements and timeline from kickoff meeting', 'COMPLETED', '2026-01-16 15:00:00', NULL, NULL, 'LOW', 'OPPORTUNITY', 'OPP005', 'SYS_USER001', 'SYSTEM', 'Y', CURRENT_TIMESTAMP, 'SYS_USER001');

-- =====================================================
-- 11. SALES TARGETS
-- =====================================================

INSERT INTO SALES_TARGETS (TARGET_ID, TARGET_YEAR, TARGET_MONTH, TARGET_AMOUNT, ACHIEVED_AMOUNT, TARGET_TYPE, USER_ID, BRANCH_ID, TENANT_ID, CREATED_DATE)
VALUES
('TGT001', 2026, 1, 50000000.00, 8000000.00, 'REVENUE', 'SYS_USER001', NULL, 'SYSTEM', CURRENT_TIMESTAMP),
('TGT002', 2026, 2, 60000000.00, 0.00, 'REVENUE', 'SYS_USER001', NULL, 'SYSTEM', CURRENT_TIMESTAMP),
('TGT003', 2026, 3, 75000000.00, 0.00, 'REVENUE', 'SYS_USER001', NULL, 'SYSTEM', CURRENT_TIMESTAMP),
('TGT004', 2026, NULL, 500000000.00, 8000000.00, 'REVENUE', NULL, 'SYS_BR001', 'SYSTEM', CURRENT_TIMESTAMP),
('TGT005', 2026, 1, 5, 1, 'DEALS', 'SYS_USER001', NULL, 'SYSTEM', CURRENT_TIMESTAMP);

-- =====================================================
-- END OF INITIAL SALES DATA
-- =====================================================

-- Summary of created data:
-- - 5 Pipeline Stages (Prospecting -> Closed Won)
-- - 5 Products (2 Software licenses, 3 Services)
-- - 5 Customers (Various industries)
-- - 5 Contacts (Primary contacts for each customer)
-- - 5 Leads (Different sources and statuses)
-- - 5 Opportunities (Various pipeline stages)
-- - 5 Opportunity Products (Linked to opportunities)
-- - 5 Quotes (Various statuses)
-- - 5 Quote Items (Line items for quotes)
-- - 5 Activities (Calls, meetings, tasks, emails, notes)
-- - 5 Sales Targets (Monthly and annual targets)
