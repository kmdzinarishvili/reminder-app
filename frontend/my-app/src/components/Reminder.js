
import {post} from '../helper/post';
import useAuth from '../hooks/useAuth';
import axios  from 'axios';

const {REACT_APP_API_BASE_URL:BASE_URL} = process.env;
const MARK_AS_COMPLETED = '/reminders/completed';
const REMINDERS = '/reminders/'
const EDIT = '';

const Reminder = ({reminder, setFetchToggle}) =>{
    const {auth} = useAuth();
    const {id, date, title, recurrence, priority} = reminder;
    const formattedDate = `${date[2]}/${date[1]}/${date[0]}`
    const markAsCompleted = async () =>{
        const response = await post({urlExtension: MARK_AS_COMPLETED,
            body:{date,id},
            accessToken:auth.accessToken
        });
        if (response.status===200){
            setFetchToggle(prev=>!prev);
        }
    }
    const redirectToEdit = ()=>{
        alert("redirecting to edit")
    }
    const deleteReminder = async ()=>{
        const response = await axios.delete(BASE_URL+REMINDERS+id,
            {
                headers: { 
                    'Content-Type': 'application/json', 
                    'Authorization': "Bearer "+ auth.accessToken,
            }, 
            withCredentials:true
            } 
        );
        if (response.status===200){
            setFetchToggle(prev=>!prev);
        }
    }

    return <div className="row">
        <div onClick={markAsCompleted}><img className="check icon" src="check_icon.png" alt="edit"/></div>
        <p>{title}</p>
        <p>{recurrence}</p>
        <p> Date: {formattedDate} </p>
        <p> Priority: {priority}</p>
        <img onClick={redirectToEdit}className="edit icon" src="edit_icon.png" alt="edit"/>
        <img onClick={deleteReminder}className="delete icon" src="trash_icon.png" alt="edit"/>
    </div>
}
export default Reminder;