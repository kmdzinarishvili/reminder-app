
import {post} from '../helper/post';
import useAuth from '../hooks/useAuth';
import axios  from 'axios';
import { useNavigate } from 'react-router-dom';
import { padWithLeadingZeros } from '../helper/padWithLeadingZeros';
const {REACT_APP_API_BASE_URL:BASE_URL} = process.env;
const MARK_AS_COMPLETED = '/reminders/completed';
const REMINDERS = '/reminders/'

function toTitleCase(str) {
    return str?.replace(
      /\w\S*/g,
      function(txt) {
        return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
      }
    );
  }

const Reminder = ({reminder, setFetchToggle, isCompleted}) =>{
    const {auth,setAuth} = useAuth();
    const {id, date, title, recurrence, priority, labels, category} = reminder;
    const formattedDate = `${date[2]}/${date[1]}/${date[0]}`
    const formattedTime = `${padWithLeadingZeros(date[3])}:${padWithLeadingZeros(date[4])}`
    const navigate = useNavigate();
    const markAsCompleted = async () =>{
        const response = await post({urlExtension: MARK_AS_COMPLETED,
            body:{date,id},
            accessToken:auth.accessToken
        }).catch(err =>{
            if(err?.response?.status===403){
                setAuth({});
                navigate('/login');
            }
        });
        if (response?.status===200){
            setFetchToggle(prev=>!prev);
        }
    }
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

    return <div className="row data-row">
        {!isCompleted&&
            <div onClick={markAsCompleted}>
                <img className="check icon" src="check_icon.png" alt="complete"/>
            </div>
        }
        <p style={{minWidth:350}}>{title}</p>
        <p style={{minWidth:200}}>{toTitleCase(category)}</p>
        <p style={{minWidth:100}}>{recurrence==="NEVER"?"One Time": toTitleCase(recurrence)}</p>
        <p style={{minWidth:200}}>{!isCompleted && <span>{formattedDate} {formattedTime} </span>} </p>
        <p style={{minWidth:150}}> Priority: {priority}</p>
        {labels.length>0&& 
            <p> Labels: {labels.map(l=>{
                return toTitleCase(l)
            }).toString()}</p>
        }
        <div className='delete-cont'>
            {!isCompleted && 
                <img onClick={redirectToEdit}className="edit icon" src="edit_icon.png" alt="edit"/>
            }
            <img  onClick={deleteReminder}className="delete icon" src="trash_icon.png" alt="delete"/>
        </div>
    </div>
}
export default Reminder;