import { Routes, Route } from 'react-router-dom';

import Register from './pages/Register';
import Login from './pages/Login';
import Home from './pages/Home';
import Layout from './pages/Layout';
import Admin from './pages/Admin';
import Unauthorized from './pages/Unauthorized';
import RequireAuth from './pages/RequireAuth';
import NotFound from './pages/NotFound';
import AddReminder from './pages/AddReminder';

import './App.css';


function App() {
  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        <Route path="login" element={<Login />} />
        <Route path="register" element={<Register />} />
        <Route path="unauthorized" element={<Unauthorized />} />

        <Route element={<RequireAuth allowedRole={"USER"} />}>
          <Route path="/" element={<Home />} />
          <Route path="/add" element={<AddReminder />} />

        </Route>

        <Route element={<RequireAuth allowedRole={"ADMIN"} />}>
          <Route path="admin" element={<Admin />} />
        </Route>

        <Route path="*" element={<NotFound />} />
      </Route>
    </Routes>
  );
}

export default App;