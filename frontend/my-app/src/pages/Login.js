import { useState } from 'react';
import useAuth from '../hooks/useAuth';
import { Link, useNavigate } from 'react-router-dom';

import axios from 'axios';
const LOGIN_URL = '/auth/authenticate';

const Login = () => {
    const { auth, setAuth } = useAuth();
    const {REACT_APP_API_BASE_URL:BASE_URL}= process.env;

    const navigate = useNavigate();

    const [email, setEmail] = useState();
    const [password, setPassword] = useState();
    const [errMsg, setErrMsg] = useState();

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const response = await axios.post(BASE_URL+LOGIN_URL,
                JSON.stringify({ email, password }),
                {
                    headers: { 'Content-Type': 'application/json' },
                }
            )
            const accessToken = response?.data?.access_token;
            const role = response?.data?.role;
            setAuth({  role, accessToken });
            if(role==="ADMIN"){
                navigate("/admin");
            }else{
                navigate("/")
            }
        } catch (err) {
            if (!err?.response) {
                setErrMsg('No Server Response');
            } else if (err.response?.status === 400||err.response?.status === 401|| err.response?.status ===403) {
                setErrMsg('Invalid Email or Password');
            } else {
                setErrMsg('Login Failed');
            }
        }
    }

    return (
        <div className='parent'>
            <section className='container'>
                <h1 className="title">Login</h1>
                <form onSubmit={handleSubmit} className='container'>
                    <label className='label' htmlFor="email">Email</label>
                    <input
                        className="input"
                        type="text"
                        id="email"
                        autoComplete="off"
                        onChange={(e) => setEmail(e.target.value)}
                        value={email}
                        required
                    />
                    <label className='label'  htmlFor="password">Password</label>
                    <input
                        className="input"
                        type="password"
                        id="password"
                        onChange={(e) => setPassword(e.target.value)}
                        value={password}
                        required
                    />
                    <p>{errMsg}</p>
                    <button className='login-btn'>Login</button>
                </form>
                <p>
                    Need an Account?
                    <br />
                    <br />
                    <span className="line">
                        <Link to="/register">Sign Up</Link>
                    </span>
                </p>
            </section>
        </div>
    )
}

export default Login
