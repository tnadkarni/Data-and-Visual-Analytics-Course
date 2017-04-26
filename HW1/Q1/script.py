import csv
import json
import time
import tweepy

# You must use Python 2.7.x
# Rate limit chart for Twitter REST API - https://dev.twitter.com/rest/public/rate-limits

def loadKeys(key_file):
    # TODO: put in your keys and tokens in the keys.json file,
    #       then implement this method for loading access keys and token from keys.json
    # rtype: str <api_key>, str <api_secret>, str <token>, str <token_secret>
    # Load keys here and replace the empty strings in the return statement with those keys
    with open(key_file) as df:
        keys = json.load(df)
    return keys['api_key'], keys['api_secret'], keys['token'], keys['token_secret'] 

# Q1.b - 5 Marks
def getFollowers(api, root_user, no_of_followers):
    # TODO: implement the method for fetching 'no_of_followers' followers of 'root_user'
    # rtype: list containing entries in the form of a tuple (follower, root_user)
    primary_followers = []
    followers = api.followers(root_user, count=no_of_followers)
    for follower in followers:
        primary_followers.append((follower._json['screen_name'], root_user))
    return primary_followers

# Q1.b - 7 Marks
def getSecondaryFollowers(api, followers_list, no_of_followers):
    # TODO: implement the method for fetching 'no_of_followers' followers for each entry in followers_list
    # rtype: list containing entries in the form of a tuple (follower, followers_list[i])  
    secondary_followers = []
    for user, root_user in followers_list:
        user_followers = [x[0] for x in getFollowers(api, user, no_of_followers)]
        secondary_followers.append((user, user_followers))
    return secondary_followers

# Q1.c - 5 Marks
def getFriends(api, root_user, no_of_friends):
    # TODO: implement the method for fetching 'no_of_friends' friends of 'root_user'
    # rtype: list containing entries in the form of a tuple (root_user, friend)
    primary_friends = []
    friends = api.friends(root_user, count=no_of_friends)
    for friend in friends:
        primary_friends.append((root_user, friend._json['screen_name']))
    return primary_friends

# Q1.c - 7 Marks
def getSecondaryFriends(api, friends_list, no_of_friends):
    # TODO: implement the method for fetching 'no_of_friends' friends for each entry in friends_list
    # rtype: list containing entries in the form of a tuple (friends_list[i], friend)
    secondary_friends = []
    for root_user, user in friends_list:
        user_friends = [x[1] for x in getFriends(api, user, no_of_friends)]
        secondary_friends.append((user_friends, user))
    return secondary_friends

# Q1.b, Q1.c - 6 Marks
def writeToFile(data, output_file):
    # write data to output_file
    # rtype: None
    file_list = []
    for x,y in data:
        if type(y) == list:
            for user in y:
                rows = {}
                rows['source'] = user
                rows['target'] = x
                file_list.append(rows)
        elif type(x) == list:
            for user in x:
                rows = {}
                rows['source'] = y
                rows['target'] = user
                file_list.append(rows)
        else:
            rows = {}
            rows['source'] = x
            rows['target'] = y
            file_list.append(rows)

    csvfile = open(output_file, 'w')
    fieldnames = ['source', 'target']
    writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
    for row in file_list:
        writer.writerow(row)
    csvfile.close()




"""
NOTE ON GRADING:

We will import the above functions
and use testSubmission() as below
to automatically grade your code.

You may modify testSubmission()
for your testing purposes
but it will not be graded.

It is highly recommended that
you DO NOT put any code outside testSubmission()
as it will break the auto-grader.

Note that your code should work as expected
for any value of ROOT_USER.
"""

def testSubmission():
    KEY_FILE = 'keys.json'
    OUTPUT_FILE_FOLLOWERS = 'followers.csv'
    OUTPUT_FILE_FRIENDS = 'friends.csv'

    ROOT_USER = 'PoloChau'
    NO_OF_FOLLOWERS = 10
    NO_OF_FRIENDS = 10


    api_key, api_secret, token, token_secret = loadKeys(KEY_FILE)

    auth = tweepy.OAuthHandler(api_key, api_secret)
    auth.set_access_token(token, token_secret)
    api = tweepy.API(auth, wait_on_rate_limit= True)

    primary_followers = getFollowers(api, ROOT_USER, NO_OF_FOLLOWERS)
    secondary_followers = getSecondaryFollowers(api, primary_followers, NO_OF_FOLLOWERS)
    followers = primary_followers + secondary_followers

    primary_friends = getFriends(api, ROOT_USER, NO_OF_FRIENDS)
    secondary_friends = getSecondaryFriends(api, primary_friends, NO_OF_FRIENDS)
    friends = primary_friends + secondary_friends

    writeToFile(followers, OUTPUT_FILE_FOLLOWERS)
    writeToFile(friends, OUTPUT_FILE_FRIENDS)


if __name__ == '__main__':
    testSubmission()


