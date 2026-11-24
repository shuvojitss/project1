import React, { createContext, useState, useEffect, useCallback, ReactNode } from 'react';
import { User, RoleName } from '@/types';
import { authApi } from '@/api/authApi';

interface AuthContextType {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  activeLocationId: string;
  setActiveLocationId: (locationId: string) => void;
  hasRole: (role: RoleName) => boolean;
  hasAnyRole: (roles: RoleName[]) => boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  switchUserForDemo: (role: RoleName) => void;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Demo seed accounts per Item 10 & 48
export const DEMO_USERS: Record<RoleName, { user: User; token: string }> = {
  ADMIN: {
    user: {
      id: '00000000-0000-0000-0000-000000000001',
      username: 'admin',
      email: 'admin@scm360.infy.com',
      fullName: 'System Administrator',
      roles: ['ADMIN'],
      locationIds: ['wh-us-east-1', 'wh-us-west-1', 'wh-eu-central-1', 'wh-ap-south-1'],
      enabled: true,
      locked: false,
    },
    token: 'demo-admin-token',
  },
  PROCUREMENT_MANAGER: {
    user: {
      id: '00000000-0000-0000-0000-000000000002',
      username: 'proc_manager',
      email: 'proc_mgr@scm360.infy.com',
      fullName: 'Procurement Manager',
      roles: ['PROCUREMENT_MANAGER'],
      locationIds: ['wh-us-east-1', 'wh-us-west-1'],
      enabled: true,
      locked: false,
    },
    token: 'demo-proc-mgr-token',
  },
  PROCUREMENT_OFFICER: {
    user: {
      id: '00000000-0000-0000-0000-000000000003',
      username: 'proc_officer',
      email: 'proc_off@scm360.infy.com',
      fullName: 'Procurement Officer',
      roles: ['PROCUREMENT_OFFICER'],
      locationIds: ['wh-us-east-1'],
      enabled: true,
      locked: false,
    },
    token: 'demo-proc-off-token',
  },
  WAREHOUSE_MANAGER: {
    user: {
      id: '00000000-0000-0000-0000-000000000004',
      username: 'wh_manager',
      email: 'wh_mgr@scm360.infy.com',
      fullName: 'Warehouse Manager',
      roles: ['WAREHOUSE_MANAGER'],
      locationIds: ['wh-us-east-1'],
      enabled: true,
      locked: false,
    },
    token: 'demo-wh-mgr-token',
  },
  WAREHOUSE_OPERATOR: {
    user: {
      id: '00000000-0000-0000-0000-000000000005',
      username: 'wh_operator',
      email: 'wh_op@scm360.infy.com',
      fullName: 'Warehouse Operator',
      roles: ['WAREHOUSE_OPERATOR'],
      locationIds: ['wh-us-east-1'],
      enabled: true,
      locked: false,
    },
    token: 'demo-wh-op-token',
  },
  LOGISTICS_PLANNER: {
    user: {
      id: '00000000-0000-0000-0000-000000000006',
      username: 'logistics_planner',
      email: 'planner@scm360.infy.com',
      fullName: 'Logistics Planner',
      roles: ['LOGISTICS_PLANNER'],
      locationIds: ['wh-us-east-1', 'wh-us-west-1'],
      enabled: true,
      locked: false,
    },
    token: 'demo-logistics-token',
  },
  SALES_COORDINATOR: {
    user: {
      id: '00000000-0000-0000-0000-000000000007',
      username: 'sales_coord',
      email: 'sales@scm360.infy.com',
      fullName: 'Sales Coordinator',
      roles: ['SALES_COORDINATOR'],
      locationIds: ['wh-us-east-1'],
      enabled: true,
      locked: false,
    },
    token: 'demo-sales-token',
  },
  SUPPLIER_USER: {
    user: {
      id: '00000000-0000-0000-0000-000000000008',
      username: 'supplier_apex',
      email: 'portal@apexcomponents.com',
      fullName: 'Apex Supplier Rep',
      roles: ['SUPPLIER_USER'],
      locationIds: [],
      supplierId: 'sup-001',
      enabled: true,
      locked: false,
    },
    token: 'demo-supplier-token',
  },
  EXECUTIVE: {
    user: {
      id: '00000000-0000-0000-0000-000000000009',
      username: 'executive',
      email: 'exec@scm360.infy.com',
      fullName: 'Executive Officer',
      roles: ['EXECUTIVE'],
      locationIds: ['wh-us-east-1', 'wh-us-west-1', 'wh-eu-central-1', 'wh-ap-south-1'],
      enabled: true,
      locked: false,
    },
    token: 'demo-exec-token',
  },
};

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(() => {
    const cached = sessionStorage.getItem('scm_user');
    return cached ? JSON.parse(cached) : DEMO_USERS.ADMIN.user;
  });
  const [token, setToken] = useState<string | null>(() => {
    return sessionStorage.getItem('scm_access_token') || DEMO_USERS.ADMIN.token;
  });
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [activeLocationId, setActiveLocationId] = useState<string>('wh-us-east-1');

  useEffect(() => {
    if (token) {
      sessionStorage.setItem('scm_access_token', token);
    }
    if (user) {
      sessionStorage.setItem('scm_user', JSON.stringify(user));
      if (user.locationIds && user.locationIds.length > 0 && !user.locationIds.includes(activeLocationId)) {
        setActiveLocationId(user.locationIds[0]);
      }
    }
  }, [user, token, activeLocationId]);

  const hasRole = useCallback(
    (role: RoleName) => {
      return !!user?.roles?.includes(role);
    },
    [user]
  );

  const hasAnyRole = useCallback(
    (roles: RoleName[]) => {
      return !!user?.roles?.some((r) => roles.includes(r));
    },
    [user]
  );

  const login = async (username: string, password: string) => {
    setIsLoading(true);
    try {
      const data = await authApi.login(username, password);
      setUser(data.user);
      setToken(data.accessToken);
      sessionStorage.setItem('scm_access_token', data.accessToken);
      sessionStorage.setItem('scm_refresh_token', data.refreshToken);
      sessionStorage.setItem('scm_user', JSON.stringify(data.user));
    } finally {
      setIsLoading(false);
    }
  };

  const logout = async () => {
    try {
      await authApi.logout();
    } finally {
      sessionStorage.clear();
      setUser(null);
      setToken(null);
    }
  };

  const switchUserForDemo = (role: RoleName) => {
    const demo = DEMO_USERS[role];
    if (demo) {
      setUser(demo.user);
      setToken(demo.token);
      sessionStorage.setItem('scm_access_token', demo.token);
      sessionStorage.setItem('scm_user', JSON.stringify(demo.user));
      if (demo.user.locationIds.length > 0) {
        setActiveLocationId(demo.user.locationIds[0]);
      }
    }
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!user,
        isLoading,
        activeLocationId,
        setActiveLocationId,
        hasRole,
        hasAnyRole,
        login,
        logout,
        switchUserForDemo,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};
