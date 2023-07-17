import { useNavigate } from "react-router-dom";
import useAuth from '../hooks/useAuth';
import axios from 'axios';

const LOGOUT_URL = '/auth/logout';

const LogoutBtn = () => {
    const navigate = useNavigate();
    const { auth, setAuth } = useAuth();
    const {REACT_APP_API_BASE_URL:BASE_URL}= process.env;


    const logout = async () => {
        const response = await axios.post(BASE_URL+LOGOUT_URL,
            null,
            {
                headers: { 
                    'Content-Type': 'application/json', 
                    'Authorization': "Bearer "+ auth.accessToken,
                }, 
                withCredentials:true
            } 
        );
        if(response.status ===200 ){
            setAuth({});
            navigate('/login');
        } 
    }

    return (
        <button onClick={logout}>Sign Out</button>
    )
}

export default LogoutBtn
