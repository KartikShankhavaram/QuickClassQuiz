const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp(functions.config().firebase);

const cors = require("cors")({ origin: true });

const Json2csvParser = require("json2csv").Parser;

exports.getTestReport = functions.https.onRequest((request, response) => {
  cors(request, response, () => {
    const db = admin.firestore();

    const testId = request.get("testId");
    const filename = request.get("filename");
    // const email = request.get("authEmail");

    if (testId.trim() === "" || filename.trim() === "")
      response.json(404, "Resource not found.");
    else {
      db.collection(`tests/${testId}/scores`)
        .get()
        .then(snapshot => {
          let result = [];
          snapshot.forEach(doc => {
            result.push(doc.data());
          }, this);

          const fields = [
            { label: "Student Name", value: "userName" },
            { label: "Roll Number", value: "userRollNumber" },
            { label: "Marks", value: "userScore" }
          ];
          const json2csvParser = new Json2csvParser({ fields });
          const csv = json2csvParser.parse(result);

          response.setHeader(
            "Content-disposition",
            `attachment; filename=${filename}`
          );
          response.set("Content-Type", "text/csv");
          response.set("Access-Control-Allow-Origin", "*");
          response.status(200).send(csv);
          // response.json(result);
        })
        .catch(reason => {
          response.json(500, `Error is ${reason}`);
        });
    }
  });
});

// async function checkForTeacherAuth(email) {
//   const teachersRef = await db.collection("teachers").get();
//   for (teacher of teachersRef.docs) {

//   }
// }
