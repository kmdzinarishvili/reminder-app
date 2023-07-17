import { useNavigate } from "react-router-dom";
import useAuth from '../hooks/useAuth';
import axios from 'axios';

const LOGOUT_URL = '/auth/logout';

const LogoutBtn = () => {
    const { auth } = useAuth();
    const {REACT_APP_API_BASE_URL:BASE_URL}= process.env;

    console.log("auth", auth)

    const logout = async () => {
        //api call for logout 
        console.log( "Bearer "+ auth.accessToken);
        console.log(BASE_URL+LOGOUT_URL)
        const response = await axios.post("/api/v1"+LOGOUT_URL,
            null,
            {
                headers: { 
                    'Content-Type': 'application/json', 
                    'Authorization': "Bearer "+ auth.accessToken,
                }, 
                withCredentials:true
            } 
        );
        console.log(response)
        console.log("after resposne")
        // setAuth({});
        // navigate('/login');
    }

    return (
        <button onClick={logout}>Sign Out</button>
    )
}

export default LogoutBtn
