import { useEffect } from 'react';
import axios from 'axios';
import Landing from './Pages/Landing'
import ThemeProvider from './Context/ThemeContext'
import { Toaster } from 'react-hot-toast';

function App() {

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
        }
      } catch (err) {
        console.log("Session invalid or expired, clearing local storage.");
        localStorage.removeItem("accessToken");
      }
    };

    initAuth();
  }, []);

  return (
    <>
      <ThemeProvider>
        <Landing />
      </ThemeProvider>
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
    </>
  )
}

export default App
