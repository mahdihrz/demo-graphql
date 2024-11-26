# demo-graphql
The goal is to familiarize with Graphql and demo what Graphql could achieve.

We will implement the backend part for Ideas app.

The code will make possible :
- view a list of ideas
- add a new Idea


## 1. What technologies would we choose?

We will try to respect the following principles as much as possible :

- **_The more code you write, the more bugs you create._**
- **_KISS Principle (Keep It Simple, Stupid)_**
- **_Code is Liability_**
- **_Any fool can write code that a computer can understand. Good programmers write code that humans can understand._**

In the first step, We will use **Springboot** as it comes with a wide and robust functionalities. We will expose API (**Spring MVC**). This is a demo project, we will not need to persist data, we can implement in memory database (**h2**). To make database abstraction we will use JPA (**Spring JPA**). Finally, Spring Graphql will handle the graphql schema and exposing it.

In a second step, we will secure the APIs. For that we will use Spring Security.

## 2. What database(s) would I choose?
H2 as explained previously.

## 3. What considerations would I make when building the API?

I will try to make the code as simple and clean as possible. I will also make sure to respect specifications and make feature by feature to make the code incremental. I will also write tests to secure the code and be sure it is working as expected.

## 4. How would I handle security of the API?
We will use Spring Security and we will use Token based authentication. So a login API will be exposed.

## 5. Write the Slack message I would send to the frontend developer explaining how to use the API

Hello,
I have finished the implementation of Ideas backend API, and it is already available in dev environment. Please find below the API endpoint and Graphql Schema :
- Endpoint : http://localhost:8080/graphql
- GraphQL Schema :
```
type Idea {
    id: ID!
    title: String!
    description: String
    author: Author!
}

type Author {
    id: ID!
    name: String!
    email: String!
    ideas: [Idea]
}

type Query {
    getAllIdeas: [Idea]
    getIdeaById(id: ID!): Idea
    getAuthors: [Author]
    getAuthorById(id: ID!): Author
}

type Mutation {
    createIdea(title: String!, description: String, authorId: ID!): Idea
    createAuthor(name: String!, email: String!): Author
    updateIdea(id: ID!, title: String, description: String, authorId: ID): Idea
    deleteIdeaById(id: ID!): String
}
```

- GraphQL Schema : 
### Example of API request to create Author : 
```
curl --location 'localhost:8080/graphql' \
--header 'Content-Type: application/json' \
--data-raw '{
    "query": "mutation {createAuthor (name: \"testName\", email:\"test@mail.me\"){ id name email } }"
}'
```

### Example of API request to get All Authors (with id, name, email) :
```
curl --location 'localhost:8080/graphql' \
--header 'Content-Type: application/json' \
--data '{
    "query": "query {getAuthors {id name email}}"
}'
```

### Example of API request to create a new Idea :
```
curl --location 'localhost:8080/graphql' \
--header 'Content-Type: application/json' \
--data '{
    "query": "mutation {createIdea (title: \"mahdi\", description:\"awesome idea\", authorId:1) { id description author {name}} }"
}'
```