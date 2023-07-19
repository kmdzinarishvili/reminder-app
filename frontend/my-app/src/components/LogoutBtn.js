import { useNavigate } from "react-router-dom";
import useAuth from '../hooks/useAuth';
import {post} from '../helper/post';

const LOGOUT_URL = '/auth/logout';

const LogoutBtn = ({style}) => {
    const navigate = useNavigate();
    const { auth, setAuth } = useAuth();

    const logout = async (e) => {
        e.preventDefault()
        const response = await post({urlExtension: LOGOUT_URL,
            body:null,
            accessToken:auth.accessToken
        }).catch(err =>{
            if(err?.response?.status===403){
                setAuth({});
                navigate('/login');
            }
        });
        if(response?.status ===200 ){
            setAuth({});
            navigate('/login');
        } 
    }

    return (
        <button className="login-btn purple" onClick={logout} style={style}>Sign Out</button>
    )
}

export default LogoutBtn
