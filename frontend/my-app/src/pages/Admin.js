import { Link } from "react-router-dom"
import LogoutBtn from "../components/LogoutBtn"

const Admin = () => {
    return (
        <section>
            <h1>Admins Page</h1>
            <br />
            <p>You must have been assigned an Admin role.</p>
            <div className="flexGrow">
                <Link to="/">Home</Link>
            </div>
            <LogoutBtn/>
        </section>
    )
}

export default Admin
