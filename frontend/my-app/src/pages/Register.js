import { useState } from 'react';
import useAuth from '../hooks/useAuth';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
const REGISTER_URL = '/auth/register';

const Register = () => {
    const  {REACT_APP_API_BASE_URL: BASE_URL} = process.env
    const { setAuth } = useAuth();

    const navigate = useNavigate();

    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [errMsg, setErrMsg] = useState('');



    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const response = await axios.post(BASE_URL+REGISTER_URL,
                JSON.stringify({ username, email, password }),
                {
                    headers: { 'Content-Type': 'application/json' },
                }
            ).catch(err =>{
                if(err?.response?.status===403){
                    setAuth({});
                    navigate('/login');
                };
              });
            const accessToken = response?.data?.access_token;
            const role = response?.data?.role;
            setAuth({  role, accessToken });
            navigate('/');
        } catch (err) {
            if (!err?.response) {
                setErrMsg('No Server Response');
            } else if (err.response?.status === 400) {
                setErrMsg('Missing Username or Password');
            } else if (err.response?.status === 401) {
                setErrMsg('Unauthorized');
            } else {
                setErrMsg('Login Failed');
            }
        }
    }

    return (
        <div className='parent'>
            <section className='container'>
                <h1 className='title'>Register</h1>
                <form onSubmit={handleSubmit} className='container'>
                    <label className='label' htmlFor="username">Username</label>
                    <input
                        className='input'
                        type="text"
                        id="username"
                        autoComplete="off"
                        onChange={(e) => setUsername(e.target.value)}
                        value={username}
                        required
                    />
                    <label className='label' htmlFor="email">Email</label>
                    <input
                        className='input'
                        type="email"
                        id="email"
                        onChange={(e) => setEmail(e.target.value)}
                        value={email}
                        required
                    />
                    <label className='label' htmlFor="password">Password</label>
                    <input
                        className='input'
                        type="password"
                        id="password"
                        onChange={(e) => setPassword(e.target.value)}
                        value={password}
                        required
                    />
                    <p>{errMsg}</p>
                    <button className='login-btn'>Register</button>
                </form>
                <p>
                    Have an Account?
                    <br />
                    <br />
                    <span className="line">
                        <Link to="/login">Login</Link>
                    </span>
                </p>
            </section>
        </div>
    )
}

export default Register
