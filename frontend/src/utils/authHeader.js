export default function authHeader() {
  // const user = JSON.parse(localStorage.getItem('user'));
  // const token = getCookie("token");
  // console.log(token);

  let token = localStorage.getItem("token");
  

  if (token) {
    token = token.replace(/['"]+/g, "");
    // console.log({ Authorization: `Bearer ${token}` });
    return { Authorization: "Bearer " + token }; // for Spring Boot back-end

    //   console.log()
    //   return { 'x-access-token': user.accessToken };       // for Node.js Express back-end
  } else {
    return {};
  }
}

//   function getCookie(cname) {
//     let name = cname + "=";
//     console.log('cookie')
//     console.log(document.cookie)
//     let decodedCookie = decodeURIComponent(document.getElementById("cookies"));
//     let ca = decodedCookie.split(';');
//     console.log(document.cookie);
//     for(let i = 0; i <ca.length; i++) {
//       let c = ca[i];
//       while (c.charAt(0) === ' ') {
//         c = c.substring(1);
//       }
//       if (c.indexOf(name) === 0) {
//         return c.substring(name.length, c.length);
//       }
//     }
//     return "";
//   }
