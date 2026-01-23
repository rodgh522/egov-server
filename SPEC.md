# CRM Project - Technical Specifications

The technical specification has been split into modular feature documents for better maintainability.

## 📚 Feature Documentation

| # | Feature | Description |
|---|---|---|
| 00 | [Overview & Architecture](docs/features/00-overview.md) | Executive summary, Tech Stack, High-level Diagram. |
| 01 | [Multi-Tenancy](docs/features/01-multi-tenancy.md) | Isolation rules, Tenant schema, Filtering strategy. |
| 02 | [Organization](docs/features/02-organization.md) | Branch, Group, Position hierarchy and schema. |
| 03 | [User Management](docs/features/03-user-management.md) | User entity, Role assignment (User/Group), Schema. |
| 04 | [Permissions](docs/features/04-permissions.md) | Menu-driven permission generation, Audit logs. |
| 05 | [Security](docs/features/05-security.md) | Authentication, RBAC implementation, Security Config. |
| 06 | [Frontend](docs/features/06-frontend.md) | React architecture, AuthContext, Dynamic Sidebar. |

---

## 🔗 Related Resources
- **Project Vision**: [project-vision.md](project-vision.md)
- **Backend Spec**: [back-spec.md](back-spec.md) (Legacy/Reference)
- **Frontend Spec**: [front-spec.md](front-spec.md) (Legacy/Reference)
