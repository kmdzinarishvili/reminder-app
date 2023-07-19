import { useNavigate } from "react-router-dom";
import { padWithLeadingZeros } from "../helper/padWithLeadingZeros";
import axios from "axios";
import useAuth from "../hooks/useAuth";
const {REACT_APP_API_BASE_URL:BASE_URL} = process.env;
const ADMIN = '/admin/'

const User = ({user, setFetchToggle}) =>{
    const {auth, setAuth} = useAuth();
    const navigate = useNavigate();
    const {username, email, lastActivityDate, registrationDate, id} = user
   
   
    const deleteUser = async ()=>{
        const response = await axios.delete(BASE_URL+ADMIN+id,
            {
                headers: { 
                    'Content-Type': 'application/json', 
                    'Authorization': "Bearer "+ auth.accessToken,
            }, 
            withCredentials:true
            } 
        ).catch(err =>{
            if(err?.response?.status===403){
                setAuth({});
                navigate('/login');
            }
        });
        if (response?.status===200){
            setFetchToggle(prev=>!prev);
        }
    }
 
    const formattedActivityDate = lastActivityDate?`${lastActivityDate[2]}/${lastActivityDate[1]}/${lastActivityDate[0]}`: null;
    const formattedActivityTime = lastActivityDate?`${padWithLeadingZeros(lastActivityDate[3])}:${padWithLeadingZeros(lastActivityDate[4])}`:null
   
    const formattedRegistrationDate = registrationDate?`${registrationDate[2]}/${registrationDate[1]}/${registrationDate[0]}`: null;
    const formattedRegistrationTime = registrationDate?`${padWithLeadingZeros(registrationDate[3])}:${padWithLeadingZeros(registrationDate[4])}`:null
   
    console.log("USR", user)
    return <tr key={user.username} >
        <td >{username} </td> 
        <td> {email}</td>
        <td> {formattedRegistrationDate} {formattedRegistrationTime}</td>
        <td >{formattedActivityDate} {formattedActivityTime} </td> 
        <td onClick={deleteUser}><img className="delete small-icon" src="trash_icon.png" alt="delete"/></td>
    </tr>
}

export default User;