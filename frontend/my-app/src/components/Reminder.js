
const Reminder = ({reminder}) =>{
    const {date, title, recurrence, priority, id} = reminder;
    const formattedDate = `${date[2]}/${date[1]}/${date[0]}`
    return <div className="row">
        <p>{title}</p>
        <p>{recurrence}</p>
        <p> Date: {formattedDate} </p>
        <p> Priority: {priority}</p>
        <button>Mark as Completed</button>
    </div>
}
export default Reminder;