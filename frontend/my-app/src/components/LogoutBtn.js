import { useNavigate } from "react-router-dom";
import useAuth from '../hooks/useAuth';
import axios from 'axios';

const LOGOUT_URL = '/auth/logout';

const LogoutBtn = () => {
    const { auth, setAuth } = useAuth();
    const navigate = useNavigate();
    const {REACT_APP_API_BASE_URL:BASE_URL}= process.env;

    console.log("auth", auth)

    const logout = async () => {
        //api call for logout 
        console.log( "Bearer "+ auth.accessToken);
        const response = await axios.post(BASE_URL+LOGOUT_URL,
            {
                headers: { 
                    'Content-Type': 'application/json', 
                    'Authorization': "Bearer "+ auth.accessToken,
                
                }, 
                withCredentials:true
                
            }
            
        );
        setAuth({});
        // navigate('/login');
    }

    return (
        <button onClick={logout}>Sign Out</button>
    )
}

export default LogoutBtn
