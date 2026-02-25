
import { useEffect } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import axios from 'axios';
import { Toaster } from 'react-hot-toast';
import { useUser } from './Context/UserContext';

import Landing from './pages/Landing';
import Dashboard from './pages/Dashboard';
import ProtectedRoute from './utils/ProtectedRoute';
import ThemeProvider from './Context/ThemeContext';

import Announcement from './components/Dashboard/Announcement';
import Employee from './components/Dashboard/Employee';
import Role from './components/Dashboard/Role';
import Task from './components/Dashboard/Task';
import Team from './components/Dashboard/Team';
import Settings from './components/Dashboard/Settings';


function App() {
  const { fetchUser, setLoading } = useUser();

  useEffect(() => {
    const initAuth = async () => {
      try {
        const res = await axios.post("http://localhost:8080/api/auth/refresh-token", {}, {
          withCredentials: true
        });

        if (res.status === 200 && res.data) {
          console.log(res.data);
          const newAccessToken = res.data;
          localStorage.setItem("accessToken", newAccessToken);
          await fetchUser();
        } else {
          setLoading(false);
        }
      } catch (err) {
        console.log("Session invalid or expired, clearing local storage.");
        localStorage.removeItem("accessToken");
        setLoading(false);
      }
    };

    initAuth();
  }, [fetchUser, setLoading]);

  return (
    <ThemeProvider>
      <Toaster
        position="top-right"
        reverseOrder={false}
        toastOptions={{
          style: {
            background: 'var(--card-bg)',
            color: 'var(--text-color)',
            border: '1px solid var(--glass-border)',
            padding: '16px',
            backdropFilter: 'blur(8px)',
          },
          success: {
            iconTheme: {
              primary: 'var(--primary-color)',
              secondary: 'var(--card-bg)',
            },
          },
        }}
      />
      <Routes>
        <Route path="/" element={<Landing />} />

        {/* Protected Dashboard Routes */}
        <Route element={<ProtectedRoute />}>
          <Route path="/dashboard" element={<Dashboard />}>
            <Route index element={<Navigate to="announcement" replace />} />
            <Route path="announcement" element={<Announcement />} />
            <Route path="employee" element={<Employee />} />
            <Route path="role" element={<Role />} />
            <Route path="task" element={<Task />} />
            <Route path="team" element={<Team />} />
            <Route path="settings" element={<Settings />} />
          </Route>
        </Route>
      </Routes>
    </ThemeProvider>
  )
}

export default App
