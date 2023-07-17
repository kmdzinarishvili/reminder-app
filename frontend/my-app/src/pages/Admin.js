import { Link } from "react-router-dom"
import LogoutBtn from "../components/LogoutBtn"
import useAuth from "../hooks/useAuth";
import axios from "axios";

const Admin = () => {
    const {auth, setAuth} = useAuth();
    const logout = async () => {
        const response = await axios.get('http://localhost:8080/api/v1/admin/login',
            {
                headers: { 
                    'Content-Type': 'application/json', 
                    'Authorization': "Bearer "+ auth.accessToken,
                }, 
            }
        );
    }

    return (
        <section>
            <h1>Admins Page</h1>
            <br />
            <p>You must have been assigned an Admin role.</p>
            <div className="flexGrow">
                <Link to="/">Home</Link>
            </div>
            <div>
            <button onClick={logout}>Get data</button>
            </div>
            <LogoutBtn/>
        </section>
    )
}

export default Admin
