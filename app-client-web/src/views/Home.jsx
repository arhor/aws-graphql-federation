import { useState } from 'react';
import { useTranslation } from 'react-i18next';

import PropTypes from 'prop-types';

import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import ChatBubbleOutlineIcon from '@mui/icons-material/ChatBubbleOutline';
import ShareIcon from '@mui/icons-material/Share';
import Avatar from '@mui/material/Avatar';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardActions from '@mui/material/CardActions';
import CardContent from '@mui/material/CardContent';
import CardHeader from '@mui/material/CardHeader';
import Chip from '@mui/material/Chip';
import Container from '@mui/material/Container';
import Divider from '@mui/material/Divider';
import Grid from '@mui/material/Grid';
import IconButton from '@mui/material/IconButton';
import Paper from '@mui/material/Paper';
import Tab from '@mui/material/Tab';
import Tabs from '@mui/material/Tabs';
import Typography from '@mui/material/Typography';

import { usePostsPage } from '@/hooks';

const PostCard = ({ post }) => {
    const [votes, setVotes] = useState(post.votes || Math.floor(Math.random() * 500));
    
    return (
        <Card sx={{ mb: 2, borderRadius: 2 }}>
            <CardHeader
                avatar={
                    <Avatar sx={{ bgcolor: `hsl(${post.id.charCodeAt(0) * 10}, 70%, 50%)` }}>
                        {(post.author || 'U')[0].toUpperCase()}
                    </Avatar>
                }
                title={`u/${post.author || post.id + 'user'}`}
                subheader={`Posted ${post.timePosted || (Math.floor(Math.random() * 10) + 1) + 'h ago'}`}
            />
            <CardContent>
                <Typography variant="h6" gutterBottom>{post.title}</Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                    {post.content || 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam in dui mauris.'}
                </Typography>
                <Box>
                    {post.tags?.map((tag, index) => (
                        <Chip key={index} label={tag} size="small" sx={{ mr: 1, mb: 1 }} />
                    ))}
                </Box>
            </CardContent>
            <CardActions disableSpacing>
                <Box display="flex" alignItems="center" mr={2}>
                    <IconButton size="small" onClick={() => setVotes(votes + 1)}>
                        <ArrowUpwardIcon fontSize="small" />
                    </IconButton>
                    <Typography variant="body2">{votes}</Typography>
                    <IconButton size="small" onClick={() => setVotes(votes - 1)}>
                        <ArrowDownwardIcon fontSize="small" />
                    </IconButton>
                </Box>
                <IconButton aria-label="comments" size="small">
                    <ChatBubbleOutlineIcon fontSize="small" />
                    <Typography variant="body2" sx={{ ml: 0.5 }}>
                        {post.comments || Math.floor(Math.random() * 50)}
                    </Typography>
                </IconButton>
                <IconButton aria-label="share" size="small">
                    <ShareIcon fontSize="small" />
                </IconButton>
            </CardActions>
        </Card>
    );
};

PostCard.propTypes = {
    post: PropTypes.shape({
        id: PropTypes.string.isRequired,
        title: PropTypes.string,
        content: PropTypes.string,
        tags: PropTypes.arrayOf(PropTypes.string),
        author: PropTypes.string,
        votes: PropTypes.number,
        comments: PropTypes.number,
        timePosted: PropTypes.string
    }).isRequired
};

export default function Home() {
    const { t } = useTranslation();
    const { data } = usePostsPage();
    const [currentTab, setCurrentTab] = useState(0);

    // Enhanced mock data for different sections
    const mockPosts = [
        {
            id: 'post1',
            title: 'How I built a scalable backend with AWS and GraphQL Federation',
            content: 'After spending months trying to optimize our backend architecture, I\'ve finally found a solution that works really well at scale. In this post, I\'ll share how we implemented GraphQL federation across multiple AWS services to create a unified API that\'s both performant and maintainable.',
            tags: ['aws', 'graphql', 'backend', 'tutorial'],
            author: 'techguru42',
            votes: 723,
            comments: 89,
            timePosted: '4h'
        },
        {
            id: 'post2',
            title: 'What do you think about the new React compiler?',
            content: 'The React team just announced their new compiler and I\'m absolutely blown away by the performance improvements. I\'ve been testing it on a few projects and seeing anywhere from 30-70% faster rendering times. Has anyone else had a chance to try it out yet?',
            tags: ['react', 'javascript', 'performance'],
            author: 'reactfan2024',
            votes: 1458,
            comments: 212,
            timePosted: '7h'
        },
        {
            id: 'post3',
            title: 'Check out my minimalist code editor theme',
            content: 'I created a new VS Code theme that\'s designed for all-day coding without eye strain. It uses a carefully selected color palette based on color psychology research.',
            tags: ['vscode', 'theme', 'productivity'],
            author: 'designdev',
            votes: 392,
            comments: 47,
            timePosted: '2d'
        },
        {
            id: 'post4',
            title: 'Netflix is hiring frontend engineers - AMA about our interview process',
            content: "I'm a senior engineer at Netflix and we're currently expanding our team. I thought it might be helpful to give some insight into what we look for and how our interview process works. Feel free to ask me anything!",
            tags: ['career', 'jobs', 'frontend', 'netflix'],
            author: 'netflixengineer',
            votes: 2102,
            comments: 345,
            timePosted: '1d'
        },
        {
            id: 'post5',
            title: 'Just launched my first SaaS product!',
            content: "After 6 months of nights and weekends, I'm excited to share that I just launched my first SaaS product - a tool that helps developers automate API documentation. It's called DocGen and you can check it out at docgen.io",
            tags: ['launch', 'saas', 'api', 'documentation'],
            author: 'solofounder',
            votes: 873,
            comments: 128,
            timePosted: '12h'
        },
        {
            id: 'post6',
            title: 'I analyzed 1000 GitHub repositories to find the most common coding patterns',
            content: "I built a tool that scanned 1000 popular open-source repositories to identify common coding patterns. I found some really interesting trends across different languages and frameworks. Here's what I discovered...",
            tags: ['github', 'research', 'coding', 'data'],
            author: 'dataanalyst',
            votes: 1687,
            comments: 203,
            timePosted: '3d'
        },
        {
            id: 'post7',
            title: 'Unpopular opinion: TypeScript is overused',
            content: "I know I'm going to get downvoted for this, but I think TypeScript is being used in projects where it doesn't add enough value to justify the overhead. For small to medium projects, especially those with small teams, the extra complexity can slow down development without providing enough benefit.",
            tags: ['typescript', 'javascript', 'opinion'],
            author: 'controversialcoder',
            votes: 42,
            comments: 311,
            timePosted: '5h'
        },
        {
            id: 'post8',
            title: 'How we reduced our bundle size by 70%',
            content: "Our team just completed a major optimization project that reduced our JavaScript bundle size by over 70%. This led to a 35% improvement in load times and a significant boost in conversion rate. Here's exactly what we did...",
            tags: ['optimization', 'webpack', 'performance'],
            author: 'perfoptimizer',
            votes: 1235,
            comments: 92,
            timePosted: '1d'
        }
    ];

    // Use either the API data if available, or the mock data
    const allPosts = (data?.posts?.data && data.posts.data.length > 0) ? data.posts.data : mockPosts;
    
    // Different post collections for different tabs
    const trendingPosts = [...allPosts].sort((a, b) => {
        const bPopularity = (b.votes || 0) * 0.7 + (b.comments || 0) * 0.3;
        const aPopularity = (a.votes || 0) * 0.7 + (a.comments || 0) * 0.3;
        return bPopularity - aPopularity;
    }).slice(0, 4);
    
    const popularPosts = [...allPosts].sort((a, b) => (b.votes || 0) - (a.votes || 0));
    
    const newPosts = [...allPosts].sort((a, b) => {
        // Sort by most recent (mock time posted logic)
        const aIsHours = a.timePosted?.includes('h') || false;
        const bIsHours = b.timePosted?.includes('h') || false;
        
        if (aIsHours && !bIsHours) return -1;
        if (!aIsHours && bIsHours) return 1;
        
        const aTime = parseInt(a.timePosted || '0', 10);
        const bTime = parseInt(b.timePosted || '0', 10);
        return aTime - bTime;
    });

    const handleTabChange = (event, newValue) => {
        setCurrentTab(newValue);
    };

    return (
        <Container maxWidth="lg" sx={{ mt: 3 }}>
            <Grid container spacing={3}>
                {/* Main content area */}
                <Grid item xs={12} md={8}>
                    <Paper sx={{ mb: 3 }}>
                        <Tabs 
                            value={currentTab} 
                            onChange={handleTabChange} 
                            indicatorColor="primary"
                            textColor="primary"
                        >
                            <Tab label={t("Trending")} />
                            <Tab label={t("Popular")} />
                            <Tab label={t("New")} />
                        </Tabs>
                    </Paper>

                    {/* Trending Tab */}
                    {currentTab === 0 && (
                        <Box>
                            {trendingPosts.length > 0 ? (
                                trendingPosts.map(post => <PostCard key={post.id} post={post} />)
                            ) : (
                                <Typography variant="body1">{t('No trending posts available')}</Typography>
                            )}
                        </Box>
                    )}

                    {/* Popular Tab */}
                    {currentTab === 1 && (
                        <Box>
                            {popularPosts.length > 0 ? (
                                popularPosts.map(post => <PostCard key={post.id} post={post} />)
                            ) : (
                                <Typography variant="body1">{t('No popular posts available')}</Typography>
                            )}
                        </Box>
                    )}

                    {/* New Tab */}
                    {currentTab === 2 && (
                        <Box>
                            {newPosts.length > 0 ? (
                                newPosts.map(post => <PostCard key={post.id} post={post} />)
                            ) : (
                                <Typography variant="body1">{t('No new posts available')}</Typography>
                            )}
                        </Box>
                    )}
                </Grid>

                {/* Sidebar */}
                <Grid item xs={12} md={4}>
                    <Box 
                        sx={{ 
                            position: 'sticky',
                            top: '80px', // Header height (64px) + 16px gap
                            maxHeight: 'calc(100vh - 80px)', // Adjusted for the gap
                            overflowY: 'auto' // Allow scrolling if content is too tall
                        }}
                    >
                        <Paper sx={{ p: 2, mb: 3 }}>
                            <Typography variant="h6" gutterBottom>{t('Community Rules')}</Typography>
                            <Divider sx={{ mb: 2 }} />
                            <Typography variant="body2" paragraph>1. {t('Be respectful to others')}</Typography>
                            <Typography variant="body2" paragraph>2. {t('No spam or self-promotion')}</Typography>
                            <Typography variant="body2" paragraph>3. {t('Post content in the right category')}</Typography>
                        </Paper>

                        <Paper sx={{ p: 2 }}>
                            <Typography variant="h6" gutterBottom>{t('Top Communities')}</Typography>
                            <Divider sx={{ mb: 2 }} />
                            <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                                <Avatar sx={{ mr: 1, width: 24, height: 24 }}>P</Avatar>
                                <Typography variant="body2">r/programming</Typography>
                            </Box>
                            <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                                <Avatar sx={{ mr: 1, width: 24, height: 24 }}>T</Avatar>
                                <Typography variant="body2">r/technology</Typography>
                            </Box>
                            <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                <Avatar sx={{ mr: 1, width: 24, height: 24 }}>W</Avatar>
                                <Typography variant="body2">r/webdev</Typography>
                            </Box>
                        </Paper>
                    </Box>
                </Grid>
            </Grid>
        </Container>
    );
}
