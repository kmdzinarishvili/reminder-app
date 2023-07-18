
import {post} from '../helper/post';
import useAuth from '../hooks/useAuth';
import axios  from 'axios';
import { useNavigate } from 'react-router-dom';
const {REACT_APP_API_BASE_URL:BASE_URL} = process.env;
const MARK_AS_COMPLETED = '/reminders/completed';
const REMINDERS = '/reminders/'

function toTitleCase(str) {
    return str.replace(
      /\w\S*/g,
      function(txt) {
        return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
      }
    );
  }

const Reminder = ({reminder, setFetchToggle, isOld}) =>{
    const {auth} = useAuth();
    const {id, date, title, recurrence, priority, labels} = reminder;
    const formattedDate = `${date[2]}/${date[1]}/${date[0]}`
    const navigate = useNavigate();
    const markAsCompleted = async () =>{
        const response = await post({urlExtension: MARK_AS_COMPLETED,
            body:{date,id},
            accessToken:auth.accessToken
        });
        if (response.status===200){
            setFetchToggle(prev=>!prev);
        }
    }
    console.log("LABELS", labels)
    const redirectToEdit = ()=>{
        navigate('/edit', {state:{reminder}})
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

    return <div className="row reminder">
        <div onClick={markAsCompleted}>
            <img className="check icon" src="check_icon.png" alt="complete"/>
        </div>
        <p>{title}</p>
        <p>{recurrence}</p>
        <p> Date: {formattedDate} </p>
        <p> Priority: {priority}</p>
        {labels.length>0&& 
            <p> Labels: {labels.map(l=>{
                console.log(l);
                console.log(toTitleCase(l));
                return toTitleCase(l)
            }).toString()}</p>
        }
        <img onClick={redirectToEdit}className="edit icon" src="edit_icon.png" alt="edit"/>
        <img onClick={deleteReminder}className="delete icon" src="trash_icon.png" alt="delete"/>
    </div>
}
export default Reminder;