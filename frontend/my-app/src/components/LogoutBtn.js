import { useNavigate } from "react-router-dom";
import useAuth from '../hooks/useAuth';
import {post} from '../helper/post';

const LOGOUT_URL = '/auth/logout';

const LogoutBtn = () => {
    const navigate = useNavigate();
    const { auth, setAuth } = useAuth();

    const logout = async () => {
        const response = await post({urlExtension: LOGOUT_URL,
            body:null,
            accessToken:auth.accessToken
        });
        if(response.status ===200 ){
            setAuth({});
            navigate('/login');
        } 
    }

    return (
        <button className="login-btn purple" onClick={logout}>Sign Out</button>
    )
}

export default LogoutBtn
