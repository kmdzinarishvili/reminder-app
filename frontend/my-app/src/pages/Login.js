import { useState } from 'react';
import useAuth from '../hooks/useAuth';
import { Link, useNavigate } from 'react-router-dom';

import axios from 'axios';
const LOGIN_URL = '/auth/authenticate';

const Login = () => {
    const { auth, setAuth } = useAuth();
    const {REACT_APP_API_BASE_URL:BASE_URL}= process.env;

    const navigate = useNavigate();

    const [username, setUsername] = useState();
    const [password, setPassword] = useState();
    const [errMsg, setErrMsg] = useState();



    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const response = await axios.post(BASE_URL+LOGIN_URL,
                JSON.stringify({ username, password }),
                {
                    headers: { 'Content-Type': 'application/json' },
                }
            );
            console.log(JSON.stringify(response));
            const accessToken = response?.data?.access_token;
            const role = response?.data?.role;
            console.log("auth", {  role, accessToken })
            setAuth({  role, accessToken });
            console.log("set auth", auth)
            if(role==="ADMIN"){
                navigate("/admin");
            }else{
                navigate("/")
            }
        } catch (err) {
            if (!err?.response) {
                setErrMsg('No Server Response');
            } else if (err.response?.status === 400) {
                setErrMsg('Missing Username or Password');
            } else if (err.response?.status === 401|| err.response?.status ===403) {
                setErrMsg('Unauthorized');
            } else {
                setErrMsg('Login Failed');
            }
        }
    }

    return (

        <section>
            <h1>Login</h1>
            <form onSubmit={handleSubmit} className='box'>
                <label htmlFor="username">Username:</label>
                <input
                    type="text"
                    id="username"
                    autoComplete="off"
                    onChange={(e) => setUsername(e.target.value)}
                    value={username}
                    required
                />
                <label htmlFor="password">Password:</label>
                <input
                    type="password"
                    id="password"
                    onChange={(e) => setPassword(e.target.value)}
                    value={password}
                    required
                />
                <p>{errMsg}</p>
                <button>Sign In</button>
            </form>
            <p>
                Need an Account?<br />
                <span className="line">
                    <Link to="/register">Sign Up</Link>
                </span>
            </p>
        </section>

    )
}

export default Login
