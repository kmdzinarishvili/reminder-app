import { Link } from "react-router-dom"

const NotFound = () => {
    return (
        <article style={{ padding: "100px" }}>
            <h1>Oops!</h1>
            <p>Page Not Found</p>
            <div className="flexGrow">
                <Link to="/login">Login</Link>
            </div>
        </article>
    )
}

export default NotFound
