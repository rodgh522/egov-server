# Feature: Frontend Architecture (React 19)

## 1. Authentication & Context
- **Storage**: Store JWT in `localStorage` (or HTTP-only cookie if possible).
- **Context**: `AuthContext` provides `user` object and helper methods:
    - `hasPermission(permCode)`
    - `hasMenuAccess(menuCode, action)`

### AuthProvider Logic
```typescript
// Fetch user on mount
useEffect(() => {
  fetchCurrentUser().then(user => setUser(user));
}, []);

// Helper
const hasPermission = (perm) => user?.permissions.includes(perm);
```

## 2. Dynamic Sidebar
- **Source**: `fetchUserMenus()` from backend.
- **Rendering**: Recursive component for nested menus.
- **State**: Highlight active route based on `location.pathname`.

## 3. Protected Routes
Wrapper component to enforce access control.

```typescript
<ProtectedRoute menuCode="business-list">
  <BusinessListPage />
</ProtectedRoute>
```

- **Logic**:
    1. Check if user is logged in.
    2. Check `hasMenuAccess(menuCode)`.
    3. Redirect to 403 or Login if failed.

## 4. UI Permission Control
Hide/Show elements based on specific actions.

```typescript
const { hasPermission } = useAuth();
{hasPermission('MENU:business:WRITE') && <Button>Create</Button>}
```
