// src/routes/routes.tsx

import { Routes, Route, Navigate } from "react-router-dom";
import Login from "../pages/login";
import Register from "../pages/register";
import LoginQR from "../pages/loginQRCODE"


function AppRoutes() {
    return (
        <Routes>
            <Route path="/" element={<Navigate to="/login" />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/login-qr" element={<LoginQR />} />
        </Routes>
    );
}

export default AppRoutes;
