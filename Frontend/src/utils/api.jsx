import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    withCredentials: true,
});

let refreshPromise = null;

const getAccessToken = () => localStorage.getItem("accessToken");

const parseJwt = (token) => {
    try {
        return JSON.parse(atob(token.split('.')[1]));
    } catch (e) {
        return null;
    }
};

api.interceptors.request.use(async (config) => {
    const token = getAccessToken();

    if (token) {
        const payload = parseJwt(token);
        const isExpiring = payload && (payload.exp * 1000) - Date.now() < 60 * 1000;

        if (isExpiring) {
            if (!refreshPromise) {
                refreshPromise = axios.post("http://localhost:8080/api/auth/refresh-token", {}, {
                    withCredentials: true
                }).then(res => {
                    if (res.status === 200) {
                        const newAccessToken = res.data;
                        localStorage.setItem("accessToken", newAccessToken);
                        return newAccessToken;
                    }
                    return null;
                }).catch(err => {
                    console.error("Token refresh failed:", err);
                    localStorage.removeItem("accessToken");
                    return null;
                }).finally(() => {
                    refreshPromise = null;
                });
            }

            const newToken = await refreshPromise;
            if (newToken) {
                config.headers.Authorization = `Bearer ${newToken}`;
            } else {
                config.headers.Authorization = `Bearer ${token}`;
            }
        } else {
            config.headers.Authorization = `Bearer ${token}`;
        }
    }

    return config;
}, (error) => {
    return Promise.reject(error);
});

export default api;
